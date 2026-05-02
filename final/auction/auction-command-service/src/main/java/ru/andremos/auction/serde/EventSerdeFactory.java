package ru.andremos.auction.serde;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.andremos.auction.model.events.AuctionEvent;

public class EventSerdeFactory {
    public static Serde<AuctionEvent> build() {
        JsonSerializer<AuctionEvent> serializer = new JsonSerializer<>();

        // Настройка десериализатора
        JsonDeserializer<AuctionEvent> deserializer = new JsonDeserializer<>(AuctionEvent.class);
        deserializer.addTrustedPackages("*"); // Разрешаем десериализацию типов

        return Serdes.serdeFrom(serializer, deserializer);
    }
}
