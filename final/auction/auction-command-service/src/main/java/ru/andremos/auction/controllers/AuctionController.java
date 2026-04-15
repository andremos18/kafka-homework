package ru.andremos.auction.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andremos.auction.dto.AuctionChangeStatusRequest;
import ru.andremos.auction.dto.AuctionCreateRequest;
import ru.andremos.auction.dto.ActionResponse;
import ru.andremos.auction.dto.AuctionDeleteRequest;
import ru.andremos.auction.dto.BidPlacedRequest;
import ru.andremos.auction.dto.UserRegisterRequest;
import ru.andremos.auction.dto.UserRegisteredResponse;
import ru.andremos.auction.model.aggregates.AuctionState;
import ru.andremos.auction.service.AuctionService;
import ru.andremos.auction.service.EventStoreService;

@RestController
@RequestMapping(path = "/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final EventStoreService eventStoreService;
    private final AuctionService auctionService;

    @GetMapping("/api/version")
    public String getVersion() {
        return "1.1";
    }

    @GetMapping("/state/{auctionId}")
    public AuctionState getAuctionState(@PathVariable Integer auctionId) {
        return eventStoreService.getAuctionState(auctionId);
    }

    @PostMapping()
    public ActionResponse createAuction(@RequestBody @Valid AuctionCreateRequest auctionCreateRequest) {
        return auctionService.createAuction(auctionCreateRequest);
    }

    @PutMapping("/{auctionId}/status")
    public ActionResponse changeStatus(@PathVariable Integer auctionId, @RequestBody @Valid AuctionChangeStatusRequest auctionChangeStatusRequest) {
        return auctionService.changeStatus(auctionId, auctionChangeStatusRequest);
    }

    @DeleteMapping()
    public ActionResponse deleteAuction(@RequestBody @Valid AuctionDeleteRequest auctionDeleteRequest) {
        return auctionService.deleteAuction(auctionDeleteRequest);
    }

    @PostMapping("/bid")
    public ActionResponse createBidPlaced(@RequestBody @Valid BidPlacedRequest bidPlacedRequest) {
        return auctionService.createBidPlaced(bidPlacedRequest);
    }

    @PostMapping("/user")
    public UserRegisteredResponse registerUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        return auctionService.registerUser(userRegisterRequest);
    }
}
