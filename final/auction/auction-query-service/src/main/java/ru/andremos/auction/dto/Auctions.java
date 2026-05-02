package ru.andremos.auction.dto;

import lombok.Data;

import java.util.List;

@Data
public class Auctions {
    private String instanceName;
    private List<Auction> auctions;
}
