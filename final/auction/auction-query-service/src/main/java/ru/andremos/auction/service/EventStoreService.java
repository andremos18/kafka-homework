package ru.andremos.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.andremos.auction.dto.Auction;
import ru.andremos.auction.dto.Auctions;
import ru.andremos.auction.model.aggregates.AuctionState;
import ru.andremos.auction.model.events.BaseEvent;
import ru.andremos.auction.model.utils.PartitionCalculator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventStoreService {

    private int port;
    private final StreamsBuilderFactoryBean eventStoreStreamsBuilder;
    private final RestClient auctionStoreClient;

    public void stopStream() {
        eventStoreStreamsBuilder.stop();
    }

    public void startStream() {
        eventStoreStreamsBuilder.start();
    }

    public Auction getUserAuction(Integer auctionId) {
        HostInfo hostInfo = getOwner(auctionId);
        String hostName = getHostName();

        log.info("this endpoint is {}:{}", hostName, port);
        log.info("host owner auctionId {} is {}:{}", auctionId, hostInfo.host(), hostInfo.port());
        AuctionState auctionState;
        if (hostName.equals(hostInfo.host()) && hostInfo.port() == port || hostInfo.host().equals("unavailable")) {
            log.info("Query auctionId: {} to local store", auctionId);
            ReadOnlyKeyValueStore<Integer, AuctionState> store = eventStoreStreamsBuilder.getKafkaStreams().store(StoreQueryParameters
                    .fromNameAndType("auction-read-state-store", QueryableStoreTypes.keyValueStore()));

            auctionState = store.get(auctionId);
        }  else {
            log.info("Query {} to {}:{}", auctionId, hostInfo.host(), hostInfo.port());
            auctionState = auctionStoreClient.get()
                    .uri("http://{host}:{port}/user/auction/{auctionId}", hostInfo.host(), hostInfo.port(), auctionId)
                    .retrieve()
                    .body(AuctionState.class);
        }

        return Auction.builder()
                .auctionId(auctionState.getAuctionId())
                .name(auctionState.getName())
                .currentPrice(auctionState.getCurrentPrice())
                .highestBidderId(auctionState.getHighestBidderId())
                .isActive(auctionState.isActive())
                .isDeleted(auctionState.isDeleted())
                .instanceName(getHostName())
                .build();

    }

    public Auctions getAllAuction() {
        KafkaStreams streams = eventStoreStreamsBuilder.getKafkaStreams();

        // Получаем доступ к Read-Only хранилищу
        ReadOnlyKeyValueStore<String, AuctionState> store = streams.store(
                StoreQueryParameters.fromNameAndType(
                        "global-auctions-store",
                        QueryableStoreTypes.keyValueStore()
                )
        );

        List<Auction> auctionList = new ArrayList<>();
        try (var it = store.all()) {
            it.forEachRemaining(aggregate -> {
                var auction = Auction.builder()
                        .auctionId(aggregate.value.getAuctionId())
                        .name(aggregate.value.getName())
                        .currentPrice(aggregate.value.getCurrentPrice())
                        //.isHighestBidder(aggregate.value.getHighestBidderId().equals(userId))
                        .highestBidderUserName(aggregate.value.getHighestBidderUserName())
                        .highestBidderId(aggregate.value.getHighestBidderId())
                        .isActive(aggregate.value.isActive())
                        .isDeleted(aggregate.value.isDeleted())
                        .build();
                auctionList.add(auction);
            });
        }

        Auctions auctions = new Auctions();
        auctions.setAuctions(auctionList);
        auctions.setInstanceName(getHostName());
        return auctions;
    }

    private HostInfo getOwner(Integer auctionId) {
        // Ищем метаданные для конкретного ключа в конкретном State Store
        StreamPartitioner<Integer, BaseEvent> customPartitioner =
                (topic, key, value, numPartitions) -> PartitionCalculator.calculateNumPartition(key, numPartitions);
        KeyQueryMetadata metadata = eventStoreStreamsBuilder.getKafkaStreams()
                .queryMetadataForKey("auction-read-state-store", auctionId, customPartitioner);

        return metadata.activeHost(); // Возвращает host и port узла
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
