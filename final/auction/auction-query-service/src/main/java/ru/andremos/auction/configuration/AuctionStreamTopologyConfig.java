package ru.andremos.auction.configuration;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.andremos.auction.model.aggregates.AuctionState;
import ru.andremos.auction.model.events.BaseEvent;
import ru.andremos.auction.model.messages.UserMessage;
import ru.andremos.auction.model.utils.PartitionCalculator;

@Configuration
public class AuctionStreamTopologyConfig {

    @Bean
    public KStream<Integer, AuctionState> auctionEvents(StreamsBuilder eventStoreStreamsBuilder,
                                                        GlobalKTable<String, UserMessage> globalUserTable) {
        StreamPartitioner<Integer, AuctionState> customPartitioner =
                (topic, key, value, numPartitions) -> PartitionCalculator.calculateNumPartition(key, numPartitions);

        KTable<Integer, AuctionState> auctionTable = eventStoreStreamsBuilder
                .stream("auction-events", Consumed.with(Serdes.Integer(), new JsonSerde<>(BaseEvent.class)))
                .groupByKey()
                .aggregate(
                        AuctionState::new, // Инициализация (Initial State)
                        (key, event, aggregate) -> aggregate.apply(event), // Накатывание (Apply)
                        Materialized.<Integer, AuctionState, KeyValueStore<Bytes, byte[]>>as("auction-read-state-store")
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(auctionSerde())
                );

        KStream<Integer, AuctionState> changelogStream = auctionTable.toStream()
                .leftJoin(globalUserTable,
                        (auctionId, auction) -> auction.getHighestBidderId() != null ? auction.getHighestBidderId() : "0",
                        (auction, user) -> {
                            String userName = (user != null) ? user.getName() : null;
                            auction.setHighestBidderUserName(userName);
                            return auction;
                        });

        changelogStream
                .mapValues(state -> {
                    if (state.isDeleted()) {
                        return null;
                    } else {
                        return state;
                    }
                })
                .to("active-auctions-snapshots", Produced.with(Serdes.Integer(),
                                new JsonSerde<>(AuctionState.class))
                        .withStreamPartitioner(customPartitioner));

        return auctionTable.toStream();
    }

    @Bean
    public GlobalKTable<Integer, AuctionState> globalAuctionTable(StreamsBuilder eventStoreStreamsBuilder) {
        return eventStoreStreamsBuilder.globalTable(
                "active-auctions-snapshots",
                Consumed.with(Serdes.Integer(), auctionSerde()),
                Materialized.<Integer, AuctionState, KeyValueStore<Bytes, byte[]>>as("global-auctions-store")
        );
    }

    @Bean
    public GlobalKTable<String, UserMessage> globalUserTable(StreamsBuilder eventStoreStreamsBuilder) {
        return eventStoreStreamsBuilder.globalTable(
                "auction-users",
                Consumed.with(Serdes.String(), new JsonSerde<>(UserMessage.class)),
                Materialized.<String, UserMessage, KeyValueStore<Bytes, byte[]>>as("auction-users-store")
        );
    }

    private Serde<AuctionState> auctionSerde() {
        return new JsonSerde<>(AuctionState.class);
    }
}
