package ru.andremos.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractConsumer implements AutoCloseable {
    protected final Thread thread = new Thread(this::process);
    private final List<String> topics;
    protected final Map<String, Object> config;
    protected final String name;

    private final KafkaConsumer<String, String> kafkaConsumer;

    public AbstractConsumer(String name, Map<String, Object> config, String... topics) {
        this.topics = List.of(topics);
        this.name = name;

        this.config = new HashMap<>(config);

        thread.setName("MessagesReceiver." + name);
        kafkaConsumer = new KafkaConsumer<>(config);
    }

    protected void onStartProcess() {
        log.info("Start!");
    }

    protected void onFinishProcess() {
        log.info("Complete!");
    }

    protected abstract void processOne(ConsumerRecord<String, String> record);

    private void process() {
        onStartProcess();

        kafkaConsumer.subscribe(topics);
        log.info("Subscribed");

        try {
            while (!Thread.interrupted()) {
                var read = kafkaConsumer.poll(Duration.ofSeconds(1));
                for (var record : read) {
                    processOne(record);
                }
            }
        } catch (WakeupException ignore) {}
        onFinishProcess();
    }

    @Override
    public void close() throws Exception {
        kafkaConsumer.wakeup();
        thread.interrupt();
        thread.join();
        kafkaConsumer.close();
    }
}
