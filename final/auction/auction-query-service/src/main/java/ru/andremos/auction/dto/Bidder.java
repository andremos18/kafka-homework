package ru.andremos.auction.dto;

import lombok.Data;

import java.util.List;

/**
 * Участник торгов
 */
@Data
public class Bidder {
    private String userId;
    private List<Auction> auctions;
}
