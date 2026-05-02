package ru.andremos.auction.dto.statistic;

import lombok.Data;

import java.util.List;

@Data
public class AuctionBetStats {
    private List<AuctionBetStat> auctionBetStats;
}
