package ru.andremos.auction.model.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuctionChangeStatusEvent extends BaseEvent {
    private boolean isActive;
}
