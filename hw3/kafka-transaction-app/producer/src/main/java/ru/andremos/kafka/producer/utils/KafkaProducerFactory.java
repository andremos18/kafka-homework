package ru.andremos.kafka.producer.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KafkaProducerFactory {

    public static final Map<String, Object> producerConfig = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ProducerConfig.ACKS_CONFIG, "all",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    public static <K,V> KafkaProducer<K,V> createProducer(Consumer<Map<String, Object>> builder) {
        var map = new HashMap<>(producerConfig);
        builder.accept(map);
        return new KafkaProducer<>(map);
    }
}
