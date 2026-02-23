package ru.andremos.kafka.producer;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class Application {

    private static final String TOPIC = "events";

    public static void main(String[] args) throws Exception {

        try (var producerService = new ProducerService()) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Программа завершена.");
                    break;
                } else  {
                    String[] message = input.split("[,;]");
                    if (message.length > 1) {
                        producerService.send(TOPIC, message[0], message[1]);
                    } else {
                        producerService.send(TOPIC, null, message[0]);
                    }
                }
            }
        }
    }
}