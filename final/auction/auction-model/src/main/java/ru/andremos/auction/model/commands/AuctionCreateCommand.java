package ru.andremos.auction.model.commands;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class AuctionCreateCommand extends BaseCommand {
    private String name;
    private BigDecimal initialPrice;
}
