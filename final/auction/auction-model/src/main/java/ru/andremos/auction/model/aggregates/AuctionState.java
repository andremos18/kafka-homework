package ru.andremos.auction.model.aggregates;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.andremos.auction.model.events.AuctionCreatedEvent;
import ru.andremos.auction.model.events.AuctionChangeStatusEvent;
import ru.andremos.auction.model.events.AuctionDeletedEvent;
import ru.andremos.auction.model.events.BaseEvent;
import ru.andremos.auction.model.events.BidPlacedEvent;

import java.math.BigDecimal;

@Data
@Slf4j
public class AuctionState {
    private Integer auctionId;
    private String name;
    private BigDecimal currentPrice;
    private String highestBidderId;
    private String highestBidderUserName;
    private boolean isActive;
    private boolean isDeleted;

    public AuctionState apply(AuctionCreatedEvent event) {
        log.debug("AuctionCreatedEvent apply. event {}", event);
        if (auctionId == null) {
            this.auctionId = event.getAuctionId();
            this.currentPrice = event.getInitialPrice();
            this.name = event.getName();
            this.isActive = true;
            this.isDeleted = false;
            log.debug("AuctionCreatedEvent applied successfully: {}", this);
        }
        return this;
    }

    public AuctionState apply(BidPlacedEvent event) {
        log.debug("BidPlacedEvent apply. event {}", event);
        if (isActive && !isDeleted && currentPrice.compareTo(event.getAmount()) < 0) {
            currentPrice = event.getAmount();
            highestBidderId = event.getBidderId();
            log.debug("BidPlacedEvent applied successfully: {}", this);
        }
        return this;
    }

    public AuctionState apply(AuctionChangeStatusEvent event) {
        log.debug("AuctionChangeStatusEvent apply. event {}", event);
        isActive = event.isActive();
        return this;
    }

    public AuctionState apply(AuctionDeletedEvent event) {
        log.debug("AuctionDeletedEvent apply. event {}", event);
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
