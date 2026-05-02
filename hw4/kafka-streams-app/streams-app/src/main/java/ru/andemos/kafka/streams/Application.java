package ru.andemos.kafka.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import ru.andemos.kafka.streams.service.EventCounterService;
import ru.andemos.kafka.streams.service.WindowInfo;
import ru.andemos.kafka.streams.utils.Utils;

import java.util.Scanner;

@Slf4j
public class Application {

    public static void main(String[] args) {
        try (var kafkaStreams = createEventCounterStream()) {
            Scanner scanner = new Scanner(System.in);
            kafkaStreams.start();
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Программа завершена.");
                    break;
                }
            }
        }
    }

    private static KafkaStreams createEventCounterStream() {
        var builder = new StreamsBuilder();
        EventCounterService eventCounterService = new EventCounterService();

        KStream<WindowInfo, Long> countStream = eventCounterService.getCountStream(builder);
        countStream.foreach((k,v) -> System.out.printf("key %s (from %s to %s): %d%n", k.key(), k.start(), k.end(), v));


        return Utils.createKafkaStream(builder, "hw4",
                b -> b.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000));
    }
}
