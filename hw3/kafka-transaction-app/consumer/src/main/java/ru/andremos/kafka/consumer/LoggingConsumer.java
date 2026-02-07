package ru.andremos.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class LoggingConsumer extends AbstractConsumer {

    public static final Map<String, Object> consumerConfig = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG, "test-consumer",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    public LoggingConsumer(String name, boolean readCommitted, String... topics) {
        super(name, consumerConfig, topics);

        this.config.put(ConsumerConfig.GROUP_ID_CONFIG, name + "-" + UUID.randomUUID());
        this.config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        if (readCommitted) {
            this.config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        }

        this.thread.start();
    }

    @Override
    protected void processOne(ConsumerRecord<String, String> record) {
        log.info("Receive {}:{} at {}", record.key(), record.value(), record.offset());
    }
}
