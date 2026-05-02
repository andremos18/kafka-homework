package ru.andremos.auction.model.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuctionCreatedEvent.class, name = "auctionCreated"),
        @JsonSubTypes.Type(value = BidPlacedEvent.class, name = "bidPlaced"),
        //@JsonSubTypes.Type(value = AuctionChangeStatusEvent.class, name = "AuctionChangeStatusEvent"),
        @JsonSubTypes.Type(value = AuctionChangeStatusEvent.class, name = "auctionChangeStatus"),
        @JsonSubTypes.Type(value = AuctionDeletedEvent.class, name = "auctionDeleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {
    private Integer auctionId;
    private OffsetDateTime dateTime = OffsetDateTime.now();
}
