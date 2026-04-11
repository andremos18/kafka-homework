package ru.andremos.auction.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.andremos.auction.model.aggregates.AuctionState;
import ru.andremos.auction.model.events.BaseEvent;

@Configuration
public class AuctionStreamTopologyConfig {

    @Bean
    public KStream<Integer, AuctionState> auctionEvents(StreamsBuilder eventStoreStreamsBuilder) {
        // Описываем логику свертки (Fold/Reduce)
        KTable<Integer, AuctionState> auctionTable = eventStoreStreamsBuilder
                .stream("auction-events", Consumed.with(Serdes.Integer(), new JsonSerde<>(BaseEvent.class)))
                //.stream("auction-events", Consumed.with(Serdes.String(), serde))
                .groupByKey()
                .aggregate(
                        AuctionState::new, // Инициализация (Initial State)
                        (key, event, aggregate) -> aggregate.apply(event), // Накатывание (Apply)
                        Materialized.<Integer, AuctionState, KeyValueStore<Bytes, byte[]>>as("auction-state-store")
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(new JsonSerde<>(AuctionState.class))
                );


        return auctionTable.toStream();
    }
}
