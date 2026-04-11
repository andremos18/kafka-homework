package ru.andremos.auction.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Auction {
    private Integer auctionId;
    private String name;
    private BigDecimal currentPrice;
    private boolean isActive;
    private boolean isDeleted;
    private String highestBidderId;
    private String instanceName;
}
