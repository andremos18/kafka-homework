package ru.andremos.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.andremos.auction.model.aggregates.AuctionState;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventStoreService {

    private int port;
    private final StreamsBuilderFactoryBean eventStoreStreamsBuilder;
    private final RestClient auctionStoreClient;

    private HostInfo getOwner(Integer auctionId) {
        // Ищем метаданные для конкретного ключа в конкретном State Store
        KeyQueryMetadata metadata = eventStoreStreamsBuilder.getKafkaStreams()
                .queryMetadataForKey("auction-state-store", auctionId, Serdes.Integer().serializer());

        return metadata.activeHost(); // Возвращает host и port узла
    }

    public AuctionState getAuctionState(Integer auctionId) {
        HostInfo hostInfo = getOwner(auctionId);
        String hostName = getHostName();
        if (hostName.equals(hostInfo.host()) && hostInfo.port() == port || hostInfo.host().equals("unavailable")) {
            log.info("Query to local store");
            ReadOnlyKeyValueStore<Integer, AuctionState> store = eventStoreStreamsBuilder.getKafkaStreams().store(StoreQueryParameters
                    .fromNameAndType("auction-state-store", QueryableStoreTypes.keyValueStore()));

            return store.get(auctionId);
        } else {
            return auctionStoreClient.get()
                    .uri("http://{host}:{port}/auction/state/{auctionId}", hostInfo.host(), hostInfo.port(), auctionId)
                    .retrieve()
                    .body(AuctionState.class);
        }
    }


    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "unknown-host";
        }
    }

    @EventListener
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        port = event.getWebServer().getPort();
    }

}
