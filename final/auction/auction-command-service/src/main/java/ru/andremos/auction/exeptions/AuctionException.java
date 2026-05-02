package ru.andremos.auction.exeptions;

public class AuctionException extends RuntimeException {
    public AuctionException(String message) {
        super(message);
    }
}
