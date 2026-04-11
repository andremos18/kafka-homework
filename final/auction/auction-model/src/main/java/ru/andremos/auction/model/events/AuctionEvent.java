package ru.andremos.auction.model.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // В JSON появится поле "type": "created"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuctionCreatedEvent.class, name = "auctionCreated"),
        @JsonSubTypes.Type(value = BidPlacedEvent.class, name = "bidPlaced")
})
public interface AuctionEvent {

}
