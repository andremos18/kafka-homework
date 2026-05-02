package ru.andremos.auction.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Аукцион не активен")
public class AuctionNotActiveException extends AuctionException {
    public AuctionNotActiveException(String message) {
        super(message);
    }
}
