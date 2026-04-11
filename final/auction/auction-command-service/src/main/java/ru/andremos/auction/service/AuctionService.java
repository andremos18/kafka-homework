package ru.andremos.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andremos.auction.dto.AuctionChangeStatusRequest;
import ru.andremos.auction.dto.AuctionCreateRequest;
import ru.andremos.auction.dto.ActionResponse;
import ru.andremos.auction.dto.AuctionDeleteRequest;
import ru.andremos.auction.dto.BidPlacedRequest;
import ru.andremos.auction.exeptions.AuctionExistsException;
import ru.andremos.auction.exeptions.AuctionNotActiveException;
import ru.andremos.auction.exeptions.BidNotValidException;
import ru.andremos.auction.model.aggregates.AuctionState;
import ru.andremos.auction.model.events.AuctionCreatedEvent;
import ru.andremos.auction.model.events.AuctionChangeStatusEvent;
import ru.andremos.auction.model.events.AuctionDeletedEvent;
import ru.andremos.auction.model.events.BidPlacedEvent;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {
    private final EventStoreService eventStoreService;
    private final ProducerEventService producerEventService;


    public ActionResponse createAuction(AuctionCreateRequest auctionCreateRequest) {
        AuctionState auctionState = eventStoreService.getAuctionState(auctionCreateRequest.getAuctionId());
        if (auctionState != null) {
            throw new AuctionExistsException("аукцион уже существует");
        }
        var event = new AuctionCreatedEvent();
        event.setAuctionId(auctionCreateRequest.getAuctionId());
        event.setName(auctionCreateRequest.getName());
        event.setInitialPrice(auctionCreateRequest.getInitialPrice());
        event.setDateTime(OffsetDateTime.now());
        producerEventService.send(event);
        ActionResponse response = new ActionResponse();
        response.setAuctionId(auctionCreateRequest.getAuctionId());
        response.setInstanceName(getInstanceId());
        return response;
    }

    public ActionResponse createBidPlaced(BidPlacedRequest bidPlacedRequest) {
        Integer auctionId = bidPlacedRequest.getAuctionId();
        AuctionState auctionState = eventStoreService.getAuctionState(auctionId);

        if (auctionState == null || !auctionState.isActive() || auctionState.isDeleted()) {
            throw new AuctionNotActiveException("аукцион не активен");
        }

        if (auctionState.getCurrentPrice().compareTo(bidPlacedRequest.getAmount()) >= 0) {
            throw new BidNotValidException("ставка должна быть больше текущей ставки");
        }

        var event = new BidPlacedEvent();
        event.setAuctionId(bidPlacedRequest.getAuctionId());
        event.setAmount(bidPlacedRequest.getAmount());
        event.setBidderId(bidPlacedRequest.getBidderId());
        event.setTimestamp(LocalDateTime.now());
        event.setDateTime(OffsetDateTime.now());

        producerEventService.send(event);

        ActionResponse response = new ActionResponse();
        response.setAuctionId(bidPlacedRequest.getAuctionId());
        response.setInstanceName(getInstanceId());
        return response;
    }

    public ActionResponse changeStatus(Integer auctionId, AuctionChangeStatusRequest auctionChangeStatusRequest) {
        var event = new AuctionChangeStatusEvent();
        event.setAuctionId(auctionId);
        event.setActive(auctionChangeStatusRequest.getIsActive());
        event.setDateTime(OffsetDateTime.now());
        producerEventService.send(event);
        ActionResponse response = new ActionResponse();
        response.setAuctionId(auctionId);
        response.setInstanceName(getInstanceId());
        return response;
    }

    public ActionResponse deleteAuction(AuctionDeleteRequest auctionDeleteRequest) {
        AuctionState auctionState = eventStoreService.getAuctionState(auctionDeleteRequest.getAuctionId());
        if (!auctionState.isDeleted()) {
            var event = new AuctionDeletedEvent();
            event.setAuctionId(auctionDeleteRequest.getAuctionId());
            event.setReason(auctionDeleteRequest.getReason());
            event.setDateTime(OffsetDateTime.now());
            producerEventService.send(event);
        }
        ActionResponse response = new ActionResponse();
        response.setAuctionId(auctionDeleteRequest.getAuctionId());
        response.setInstanceName(getInstanceId());
        return response;
    }

    private String getInstanceId() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "unknown-host";
        }
    }
}
