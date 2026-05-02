package ru.andemos.kafka.streams.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class Utils {

    public static final String TEST_TOPIC = "events";

    public static final Map<String, Object> streamsConfig = Map.of(
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

    public static StreamsConfig createStreamsConfig(Consumer<Map<String, Object>> builder) {
        var map = new HashMap<>(streamsConfig);
        builder.accept(map);
        return new StreamsConfig(map);
    }

    public static KafkaStreams createKafkaStream(StreamsBuilder builder, String name,
                                      Consumer<Map<String, Object>> configBuilder) {
        var topology = builder.build();
        log.info("{}", topology.describe());

        return new KafkaStreams(topology, Utils.createStreamsConfig(b -> {
            b.put(StreamsConfig.APPLICATION_ID_CONFIG, name + "-" + UUID.randomUUID().hashCode());
            b.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
            configBuilder.accept(b);
        }));
    }
}
