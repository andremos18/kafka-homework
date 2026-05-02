package ru.andremos.auction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuctionChangeStatusRequest {
    @NotNull
    private Boolean isActive;
}
