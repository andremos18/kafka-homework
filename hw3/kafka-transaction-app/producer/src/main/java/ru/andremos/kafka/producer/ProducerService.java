package ru.andremos.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.andremos.kafka.producer.utils.KafkaProducerFactory;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ProducerService implements AutoCloseable {

    private final KafkaProducer<String,String> producer;

    public ProducerService() {
        producer = KafkaProducerFactory.createProducer(
                b -> b.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString()));
    }

    public void send(String topic, String message) {
        log.info("Отправлено в топик: {} сообщение: {}", topic, message);
        producer.send(new ProducerRecord<>(topic, message));
    }

    public void initTransactions() {
        log.info("initTransactions");
        producer.initTransactions();
    }

    public void beginTransaction() {
        log.info("beginTransaction");
        producer.beginTransaction();
    }

    public void commitTransaction() {
        log.info("commitTransaction");
        producer.commitTransaction();
    }

    public void abortTransaction() {
        log.info("abortTransaction");
        producer.abortTransaction();
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }
}
