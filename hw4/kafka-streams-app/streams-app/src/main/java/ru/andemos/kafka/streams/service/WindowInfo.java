package ru.andemos.kafka.streams.service;

import java.time.OffsetDateTime;

public record WindowInfo(String key, OffsetDateTime start, OffsetDateTime end) {}
