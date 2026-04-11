package ru.andremos.auction.utils;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.utils.Utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UniformIntegerPartitioner implements Partitioner {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {

        int numPartitions = cluster.partitionsForTopic(topic).size();

        // Логика для целочисленных ключей
        if (key instanceof Number) {
            long longKey = ((Number) key).longValue();
            return (int) (Math.abs(longKey) % numPartitions);
        }

        if (keyBytes == null) {
            // используем Round Robin
            return org.apache.kafka.common.utils.Utils.toPositive(counter.getAndIncrement()) % numPartitions;
        }

        // Хешируем байты ключа как это делает стандартная Kafka
        return org.apache.kafka.common.utils.Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
    }

    @Override public void close() {}
    @Override public void configure(Map<String, ?> configs) {}
}
