package ru.andremos.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import ru.andremos.kafka.producer.utils.Utils;

import java.util.Scanner;

@Slf4j
public class Application {

    private static volatile boolean running = true;

    public static void main(String[] args) throws Exception {

        try (var producerService = new ProducerService()) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Получен сигнал завершения!");
                running = false;
            }));

            //Utils.recreateTopics(1, 1, "topic1", "topic2");
            Scanner scanner = new Scanner(System.in);
            producerService.initTransactions();
            while (running) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Программа завершена.");
                    break;
                } else if (input.equalsIgnoreCase("start")) {
                    sendRecords(producerService);
                }
            }
        }
    }

    private static void sendRecords(ProducerService producerService) {
        producerService.beginTransaction();
        for (int i = 0; i < 5; ++i) {
            producerService.send("topic1", "topic1-committed" + i);
            producerService.send("topic2", "topic2-committed" + i);
        }
        producerService.commitTransaction();

        producerService.beginTransaction();
        for (int i = 0; i < 2; ++i) {
            producerService.send("topic1", "topic1-revoked" + i);
            producerService.send("topic2", "topic2-revoked" + i);
        }
        producerService.abortTransaction();
    }
}