package ru.andremos.auction.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andremos.auction.dto.Auction;
import ru.andremos.auction.dto.Auctions;
import ru.andremos.auction.service.EventStoreService;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {

    private final EventStoreService eventStoreService;

    @GetMapping("/auction/{auctionId}")
    public Auction getUserAuction(@PathVariable Integer auctionId) {
        return eventStoreService.getUserAuction(auctionId);
    }

    @GetMapping("/auctions")
    public Auctions getAuctions() {
        return eventStoreService.getAllAuction();
    }
}
