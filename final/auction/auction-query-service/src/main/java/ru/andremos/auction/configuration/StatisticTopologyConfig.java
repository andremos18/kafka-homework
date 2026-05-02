package ru.andremos.auction.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.andremos.auction.model.aggregates.AuctionState;
import ru.andremos.auction.model.events.AuctionCreatedEvent;
import ru.andremos.auction.model.events.AuctionDeletedEvent;
import ru.andremos.auction.model.events.BaseEvent;
import ru.andremos.auction.model.events.BidPlacedEvent;
import ru.andremos.auction.statistic.BetDurationStat;

import java.time.Duration;

@Configuration
public class StatisticTopologyConfig {

    @Bean
    public KStream<Integer, BetDurationStat> statisticBet(StreamsBuilder eventStoreStreamsBuilder, GlobalKTable<Integer, AuctionState> globalAuctionTable) {

        KStream<Integer, BetDurationStat> betDurationStat = eventStoreStreamsBuilder
                .stream("auction-events", Consumed.with(Serdes.Integer(), new JsonSerde<>(BaseEvent.class)))
                .groupByKey(Grouped.with(Serdes.Integer(), new JsonSerde<>(BaseEvent.class)))
                .aggregate(
                        BetDurationStat::new,
                        (key, event, stats) -> {
                            if (event instanceof AuctionCreatedEvent create && stats.getStartTime() == null) {
                                stats.setAuctionId(create.getAuctionId());
                                stats.setAuctionName(create.getName());
                                stats.setStartTime(create.getDateTime());
                            } else if (event instanceof BidPlacedEvent bid && stats.getStartTime() != null) {
                                // считаем задержку
                                long duration = Duration.between(stats.getStartTime(), bid.getDateTime()).getSeconds();

                                // обновляем общую длительность
                                stats.setTotalDuration(duration);
                                stats.setCount(stats.getCount() == null ? 1 : stats.getCount() + 1);
                            } else if (event instanceof AuctionDeletedEvent) {
                                stats.setDeleted(true);
                                return stats;
                            }
                            return stats;
                        },
                        Materialized.<Integer, BetDurationStat, KeyValueStore<Bytes, byte[]>>as("bet-stat")
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(new JsonSerde<>(BetDurationStat.class))
                ).toStream();

        betDurationStat.mapValues(state -> {
                    if (state.isDeleted()) {
                        return null;
                    } else {
                        return state;
                    }
                })
                .to("bet-stat-snapshots", Produced.with(Serdes.Integer(), new JsonSerde<>(BetDurationStat.class)));

        //System.out.println(eventStoreStreamsBuilder.build().describe());

        return betDurationStat;
    }

    @Bean
    public GlobalKTable<String, BetDurationStat> globalBetDurationStat(StreamsBuilder eventStoreStreamsBuilder) {
        return eventStoreStreamsBuilder.globalTable(
                "bet-stat-snapshots",
                Consumed.with(Serdes.String(), new JsonSerde<>(BetDurationStat.class)),
                Materialized.<String, BetDurationStat, KeyValueStore<Bytes, byte[]>>as("global-bet-stat-store")
        );
    }

    private record AuctionBetLatency(Integer auctionId, String name, Long duration) {};
}
