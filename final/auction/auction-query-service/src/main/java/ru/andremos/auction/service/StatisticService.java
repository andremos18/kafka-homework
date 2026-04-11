package ru.andremos.auction.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;
import ru.andremos.auction.dto.statistic.AuctionBetStat;
import ru.andremos.auction.dto.statistic.AuctionBetStats;
import ru.andremos.auction.statistic.BetDurationStat;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StreamsBuilderFactoryBean eventStoreStreamsBuilder;
    public AuctionBetStats getAuctionBetStats() {
        ReadOnlyKeyValueStore<String, BetDurationStat> store =
                eventStoreStreamsBuilder.getKafkaStreams()
                        .store(StoreQueryParameters.fromNameAndType("global-bet-stat-store",
                                QueryableStoreTypes.keyValueStore()));

        AuctionBetStats stats = new AuctionBetStats();
        List<AuctionBetStat> auctionBetStats = new ArrayList<>();
        stats.setAuctionBetStats(auctionBetStats);

        try (var it = store.all()) {
            it.forEachRemaining(stat -> {
                AuctionBetStat auctionBetStat = new AuctionBetStat();
                auctionBetStat.setAuctionId(stat.value.getAuctionId());
                auctionBetStat.setAuctionName(stat.value.getAuctionName());
                auctionBetStat.setTotalDuration(stat.value.getTotalDuration());
                auctionBetStat.setCount(stat.value.getCount());
                if (stat.value.getCount() > 0) {
                    auctionBetStat.setAvgTime((double) stat.value.getTotalDuration() / stat.value.getCount());
                }
                auctionBetStats.add(auctionBetStat);
            });
        }

        return stats;
    }
}
