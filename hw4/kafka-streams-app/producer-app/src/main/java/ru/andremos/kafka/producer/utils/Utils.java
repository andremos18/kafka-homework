package ru.andremos.kafka.producer.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class Utils {

    public static final String HOST = "localhost:9092";

    public static final Map<String, Object> adminConfig = Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);

    public static void doAdminAction(AdminClientConsumer action) {
        try (var client = Admin.create(Utils.adminConfig)) {
            action.accept(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface AdminClientConsumer {
        void accept(Admin client) throws Exception;
    }

    public static void recreateTopics(int numPartitions, int replicationFactor, String ... topics) {
        doAdminAction(admin -> {
            RemoveAll.removeAll(admin);
            admin.createTopics(Stream.of(topics)
                    .map(it -> new NewTopic(it, numPartitions, (short)replicationFactor))
                    .toList());
        });
    }
}
