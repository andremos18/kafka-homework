package ru.andremos.auction.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;
import ru.andremos.auction.model.messages.UserMessage;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class UserRegisterSerializer implements Serializer<UserMessage> {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public byte[] serialize(String s, UserMessage userMessage) {
        return objectMapper.writeValueAsString(userMessage).getBytes(StandardCharsets.UTF_8);
    }
}
