package ru.andremos.auction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AuctionCreateRequest {
    @NotNull
    private Integer auctionId;
    @NotBlank
    private String name;
    @NotNull
    private BigDecimal initialPrice;
}
