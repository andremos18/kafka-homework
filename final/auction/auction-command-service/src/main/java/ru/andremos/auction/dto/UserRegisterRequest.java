package ru.andremos.auction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
}
