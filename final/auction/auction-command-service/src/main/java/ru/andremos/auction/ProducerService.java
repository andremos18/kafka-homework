package ru.andremos.auction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import ru.andremos.auction.utils.KafkaProducerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Slf4j
@RequiredArgsConstructor
public class ProducerService implements AutoCloseable {

    private final KafkaProducer<String,String> producer;

    public ProducerService() {
        producer = KafkaProducerFactory.createProducer(b -> {});
    }

    public void send(String topic, String key, String message) {
        log.info("Отправлено в топик: {} сообщение: {}", topic, message);
        Callback callback = (RecordMetadata r, Exception e) -> {
            Instant instant = Instant.ofEpochMilli(r.timestamp());
            OffsetDateTime time = OffsetDateTime.ofInstant(instant, ZoneId.of("UTC+03:00"));
            System.out.println("Record timestamp: " + time);

            if (e != null) {
                log.error(e.getMessage(), e);
            }
        };
        producer.send(new ProducerRecord<>(topic, key, message), callback);
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }
}
