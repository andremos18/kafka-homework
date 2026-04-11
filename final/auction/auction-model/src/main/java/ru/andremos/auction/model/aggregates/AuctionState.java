package ru.andremos.auction.model.aggregates;

import lombok.Data;
import ru.andremos.auction.model.events.AuctionCreatedEvent;
import ru.andremos.auction.model.events.AuctionChangeStatusEvent;
import ru.andremos.auction.model.events.AuctionDeletedEvent;
import ru.andremos.auction.model.events.BaseEvent;
import ru.andremos.auction.model.events.BidPlacedEvent;

import java.math.BigDecimal;

@Data
public class AuctionState {
    private Integer auctionId;
    private String name;
    private BigDecimal currentPrice;
    private String highestBidderId;
    private boolean isActive;
    private boolean isDeleted;

    public AuctionState apply(AuctionCreatedEvent event) {
        if (auctionId == null) {
            this.auctionId = event.getAuctionId();
            this.currentPrice = event.getInitialPrice();
            this.name = event.getName();
            this.isActive = true;
            this.isDeleted = false;
        }
        return this;
    }

    public AuctionState apply(BidPlacedEvent event) {
        if (isActive && !isDeleted && currentPrice.compareTo(event.getAmount()) < 0) {
            currentPrice = event.getAmount();
            highestBidderId = event.getBidderId();
        }
        return this;
    }

    public AuctionState apply(AuctionChangeStatusEvent event) {
        isActive = event.isActive();
        return this;
    }

    public AuctionState apply(AuctionDeletedEvent event) {
        this.isDeleted = true;
        this.isActive = false;
        return this;
    }

    public AuctionState apply(BaseEvent event) {
        if (event instanceof AuctionCreatedEvent) {
            return apply((AuctionCreatedEvent)event);
        } else if (event instanceof BidPlacedEvent) {
            return apply((BidPlacedEvent)event);
        } else if (event instanceof AuctionChangeStatusEvent) {
            return apply((AuctionChangeStatusEvent)event);
        } else if (event instanceof AuctionDeletedEvent) {
            return apply((AuctionDeletedEvent)event);
        }
        return this;
    }

}
