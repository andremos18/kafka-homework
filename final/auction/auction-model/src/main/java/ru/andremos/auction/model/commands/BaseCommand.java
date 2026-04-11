package ru.andremos.auction.model.commands;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public abstract class BaseCommand {
    private UUID auctionId;
}
