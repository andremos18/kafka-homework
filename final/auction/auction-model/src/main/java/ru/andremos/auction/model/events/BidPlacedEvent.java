package ru.andremos.auction.model.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class BidPlacedEvent extends BaseEvent {
    private String bidderId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
