package ru.andremos.auction.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andremos.auction.exeptions.AuctionNotActiveException;
import ru.andremos.auction.exeptions.BidNotValidException;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(AuctionNotActiveException.class)
    public ResponseEntity<ResponseError> handleAuctionNotActiveException(AuctionNotActiveException ex) {
        ResponseError responseError = new ResponseError(ex.getMessage());
        return new ResponseEntity<>(responseError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BidNotValidException.class)
    public ResponseEntity<ResponseError> handleBidNotValidException(BidNotValidException ex) {
        ResponseError responseError = new ResponseError(ex.getMessage());
        return new ResponseEntity<>(responseError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public record ResponseError(String message) {}

}
