package ru.andremos.auction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andremos.auction.configuration.properties.EventStoreProperties;
import ru.andremos.auction.model.events.BaseEvent;
import ru.andremos.auction.model.messages.UserMessage;

@Service
@RequiredArgsConstructor
public class ProducerEventService {
    private final KafkaTemplate<Integer, BaseEvent> eventStoreKafkaTemplate;
    private final KafkaTemplate<String, UserMessage> userRegisterKafkaTemplate;
    private final EventStoreProperties eventStoreProperties;

    public void send(BaseEvent baseEvent) {
        eventStoreKafkaTemplate.send(eventStoreProperties.getEventsTopic(), baseEvent.getAuctionId(), baseEvent);
    }

    public void send(UserMessage userMessage) {
        userRegisterKafkaTemplate.send(eventStoreProperties.getUserRegisterTopic(), userMessage.getId(), userMessage);
    }
}
