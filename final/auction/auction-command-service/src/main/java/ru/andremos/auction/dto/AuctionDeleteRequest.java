package ru.andremos.auction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuctionDeleteRequest {
    @NotNull
    private Integer auctionId;
    @NotBlank
    private String reason;
}
