package ru.andremos.auction.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Некорректная ставка")
public class BidNotValidException extends AuctionException {
    public BidNotValidException(String message) {
        super(message);
    }
}
