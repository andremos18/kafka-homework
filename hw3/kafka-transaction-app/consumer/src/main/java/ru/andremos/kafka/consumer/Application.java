package ru.andremos.kafka.consumer;

import java.util.Scanner;

public class Application {
    private static volatile boolean running = true;

    public static void main(String[] args) throws Exception {

        try (var consumer = new LoggingConsumer("1", true, "topic1", "topic2")) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Получен сигнал завершения!");
                running = false;
                try {
                    consumer.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));

            Scanner scanner = new Scanner(System.in);

            while (running) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Программа завершена.");
                    break;
                }
            }

        }
    }
}
