package ru.andremos.auction.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Аукцион уже существует")
public class AuctionExistsException extends RuntimeException {
    public AuctionExistsException(String message) {
        super(message);
    }
}
