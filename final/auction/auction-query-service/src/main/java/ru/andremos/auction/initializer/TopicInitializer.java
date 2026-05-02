package ru.andremos.auction.initializer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class TopicInitializer {
    private final KafkaAdmin kafkaAdmin;

    @EventListener(ApplicationReadyEvent.class)
    public void createTopics() {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            NewTopic ActiveAuctionTopic = TopicBuilder.name("active-auctions-snapshots")
                    .partitions(3)
                    .replicas(3)
                    .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
                    // Минимальное время, когда сегмент может быть сжат
                    .config(TopicConfig.MIN_COMPACTION_LAG_MS_CONFIG, "0")
                    // Максимальный размер сегмента (чем меньше, тем чаще запуск очистки)
                    .config(TopicConfig.SEGMENT_MS_CONFIG, "600000") // 10 минут
                    .config(TopicConfig.SEGMENT_BYTES_CONFIG, "10485760") // 10 MB
                    .build();

            client.createTopics(Collections.singleton(ActiveAuctionTopic));

            NewTopic BetStatTopic = TopicBuilder.name("bet-stat-snapshots")
                    .partitions(3)
                    .replicas(3)
                    .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
                    // Минимальное время, когда сегмент может быть сжат
                    .config(TopicConfig.MIN_COMPACTION_LAG_MS_CONFIG, "0")
                    // Максимальный размер сегмента (чем меньше, тем чаще запуск очистки)
                    .config(TopicConfig.SEGMENT_MS_CONFIG, "600000") // 10 минут
                    .config(TopicConfig.SEGMENT_BYTES_CONFIG, "10485760") // 10 MB
                    .build();

            client.createTopics(Collections.singleton(BetStatTopic));
        }
    }
}
