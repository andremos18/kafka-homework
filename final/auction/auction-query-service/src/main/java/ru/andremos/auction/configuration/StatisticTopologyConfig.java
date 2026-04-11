package ru.andremos.auction.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.andremos.auction.model.events.AuctionCreatedEvent;
import ru.andremos.auction.model.events.BaseEvent;
import ru.andremos.auction.model.events.BidPlacedEvent;
import ru.andremos.auction.statistic.BetDurationStat;

import java.time.Duration;

@Configuration
public class StatisticTopologyConfig {

    @Bean
    public KTable<Integer, BetDurationStat> statisticBet(StreamsBuilder eventStoreStreamsBuilder) {
        // 1. Поток публикаций (ключ - идентификатор аукциона)
        KTable<Integer, AuctionCreatedEvent> createdEvent = eventStoreStreamsBuilder.stream("auction-events",
                        Consumed.with(Serdes.Integer(), new JsonSerde<>(BaseEvent.class)))
                .filter((key, event) -> event instanceof AuctionCreatedEvent)
                .mapValues((k,v) -> (AuctionCreatedEvent)v)
                .groupByKey()
                .reduce((first, current) -> first,
                        Materialized.<Integer, AuctionCreatedEvent, KeyValueStore<Bytes, byte[]>>as("stat-auction-store")
                                .withKeySerde(Serdes.Integer()).withValueSerde(new JsonSerde<>(AuctionCreatedEvent.class))); // Оставляем только раннее событие создания аукциона

        // 2. Поток крайних ставок (фильтруем только самую крайню ставку для аукциона)
        KTable<Integer, BaseEvent> endBet = eventStoreStreamsBuilder.stream("auction-events",
                        Consumed.with(Serdes.Integer(), new JsonSerde<>(BaseEvent.class)))
                .filter((key, event) -> event instanceof BidPlacedEvent)
                .groupByKey()
                .reduce((first, current) -> current, Materialized.as("stat-endBet-store")); // Оставляем только самую позднюю ставку


        KTable<Integer, AuctionBetLatency> latencies = createdEvent.join(endBet,
                (auction, bet) -> new AuctionBetLatency(auction.getAuctionId(), auction.getName(),
                        Duration.between(auction.getDateTime(), bet.getDateTime()).getSeconds()),
                Named.as("join-create_event-bid_placed_event"),
                Materialized.<Integer, AuctionBetLatency, KeyValueStore<Bytes, byte[]>>
                        as("latencies-stat").withKeySerde(Serdes.Integer()).withValueSerde(new JsonSerde<>(AuctionBetLatency.class)));

        KTable<Integer, BetDurationStat> betDurationStat =
                latencies.toStream().groupByKey(Grouped.with(Serdes.Integer(), new JsonSerde<>(AuctionBetLatency.class)))
                .aggregate(() -> new BetDurationStat(null, null,0L, 0L),
                        (key, diff, stats) -> {
                            stats.setAuctionId(diff.auctionId);
                            stats.setAuctionName(diff.name);
                            stats.setTotalDuration(stats.getTotalDuration() + diff.duration());
                            stats.setCount(stats.getCount() + 1);
                            return stats;
                        }
                        ,
                        Materialized.<Integer, BetDurationStat, KeyValueStore<Bytes, byte[]>>
                                as("bet-stat").withKeySerde(Serdes.Integer()).withValueSerde(new JsonSerde<>(BetDurationStat.class))
                        );


        KStream<Integer, BetDurationStat> changelogStream = betDurationStat.toStream();
        changelogStream.to("bet-stat-snapshots", Produced.with(Serdes.Integer(),
                new JsonSerde<>(BetDurationStat.class)));

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
