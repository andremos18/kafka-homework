package ru.andremos.auction.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import ru.andremos.auction.model.events.BaseEvent;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class EventStoreSerializer implements Serializer<BaseEvent> {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public byte[] serialize(String s, BaseEvent baseEvent) {
        return objectMapper.writeValueAsString(baseEvent).getBytes(StandardCharsets.UTF_8);
    }
}
