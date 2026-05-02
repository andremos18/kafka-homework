package ru.andremos.auction.model.aggregates;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class AuctionUserState {
   private AuctionState auctionState;
   private String highestBidderUserName;
}
