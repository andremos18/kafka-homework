package ru.andemos.kafka.streams.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static ru.andemos.kafka.streams.utils.Utils.TEST_TOPIC;

@Slf4j
public class EventCounterService {

    private static final ZoneId TIME_ZONE = ZoneId.of("UTC+03:00");

    public KStream<WindowInfo, Long> getCountStream(StreamsBuilder builder) {
        Serde<String> stringSerde = Serdes.String();

        var twentySeconds = Duration.ofSeconds(20);
        var fifteenMinutes = Duration.ofMinutes(15);

        var timeDifference = Duration.ofMinutes(5);
        var afterWindowEnd = Duration.ofSeconds(30);

        KTable<Windowed<String>, Long> eventsCounts = builder
                .stream(TEST_TOPIC, Consumed.with(stringSerde, stringSerde))
                .groupBy((key, event) -> key,
                        Grouped.with(stringSerde, stringSerde))
                //.windowedBy(SessionWindows.ofInactivityGapWithNoGrace(timeDifference))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(timeDifference))
                //.windowedBy(SlidingWindows.ofTimeDifferenceWithNoGrace(timeDifference))
//                .windowedBy(SlidingWindows.ofTimeDifferenceAndGrace(timeDifference, afterWindowEnd))
                .count();
        eventsCounts.toStream()
                .foreach((k, v) -> {
                    log.info("Window {}: {}", k, v);
                });


        return eventsCounts.toStream().map((window, count) -> {
            String key = window.key();
            OffsetDateTime start = OffsetDateTime.ofInstant(window.window().startTime(), TIME_ZONE);
            OffsetDateTime end = OffsetDateTime.ofInstant(window.window().endTime(), TIME_ZONE);
            window.window().startTime();
            WindowInfo windowInfo = new WindowInfo(key, start, end);

            return KeyValue.pair(windowInfo, count);
        });
    }
}
