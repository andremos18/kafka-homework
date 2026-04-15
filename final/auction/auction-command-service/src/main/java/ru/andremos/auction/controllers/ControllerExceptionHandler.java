package ru.andremos.auction.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.andremos.auction.exeptions.AuctionException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(AuctionException.class)
    public ResponseEntity<ResponseError> handleAuctionException(AuctionException ex) {
        ResponseError responseError = new ResponseError(ex.getMessage());
        return new ResponseEntity<>(responseError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public record ResponseError(String message) {}

}
