package ru.andremos.auction.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "аукцион не найден")
public class AuctionNotFoundException extends AuctionException {
    public AuctionNotFoundException(String message) {
        super(message);
    }
}
