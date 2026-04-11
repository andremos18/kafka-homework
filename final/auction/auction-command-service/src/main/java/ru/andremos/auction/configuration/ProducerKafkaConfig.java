package ru.andremos.auction.configuration;

import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import ru.andremos.auction.configuration.properties.EventStoreProperties;
import ru.andremos.auction.model.events.BaseEvent;

@Configuration
public class ProducerKafkaConfig {

    @Bean
    public KafkaTemplate<Integer, BaseEvent> eventStoreKafkaTemplate(EventStoreProperties properties,
                                                                    EventStoreSerializer serializer) {
        final var props = properties.getKafka().buildProducerProperties();
        props.put("partitioner.class", "ru.andremos.auction.utils.UniformIntegerPartitioner");
        final var producerFactory = new DefaultKafkaProducerFactory<>(props, new IntegerSerializer(), serializer);

        return new KafkaTemplate<>(producerFactory);
    }
}
