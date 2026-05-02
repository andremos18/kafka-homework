package ru.andremos.auction.model.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class AuctionCreatedEvent extends BaseEvent {
    private String name;
    private BigDecimal initialPrice;
}
