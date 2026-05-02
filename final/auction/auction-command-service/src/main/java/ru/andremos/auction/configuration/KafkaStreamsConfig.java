package ru.andremos.auction.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import ru.andremos.auction.configuration.properties.EventStoreProperties;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaStreamsConfig {

    private final Environment env;

    @Bean
    public StreamsBuilderFactoryBean eventStoreStreamsBuilder(EventStoreProperties eventStoreProperties) {
        Map<String, Object> props = eventStoreProperties.getKafka().buildStreamsProperties(null);
        String hostName = getHostName();
        String port = env.getProperty("server.port");
        props.put(StreamsConfig.STATE_DIR_CONFIG, System.getProperty("user.dir") + "/kafka-state-" + hostName);
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "client-" + hostName + "-" + port);
        props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, hostName + ":" + port);

        //  количество «горячих» копий состояния (State Store), которые поддерживаются на других экземплярах приложения.
        props.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1);


        props.put("group.instance.id", "instance-command-"+hostName);

        var factoryBean = new StreamsBuilderFactoryBean(new KafkaStreamsConfiguration(props));
        factoryBean.setStateListener((newState, oldState) -> {
            System.out.println("Kafka Streams state changed from " + oldState + " to " + newState);
        });

        return factoryBean;
    }

    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "unknown-host";
        }
    }
}