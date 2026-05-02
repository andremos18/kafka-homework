package ru.andremos.auction.configuration.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "event-store")
public class EventStoreProperties {
    private String topic;
    private KafkaProperties kafka;
}
