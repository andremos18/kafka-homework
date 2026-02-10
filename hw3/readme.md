# Разработка приложения с транзакциями

## Цель
Научиться самостоятельно разрабатывать и запускать приложения с транзакциями.

## Описание/Пошаговая инструкция выполнения домашнего задания:
* Запустить Kafka
* Создать два топика: topic1 и topic2
* Разработать приложение, которое:
  * открывает транзакцию
  * отправляет по 5 сообщений в каждый топик
  * подтверждает транзакцию
  * открывает другую транзакцию
  * отправляет по 2 сообщения в каждый топик
  * отменяет транзакцию
* Разработать приложение, которое будет читать сообщения из топиков topic1 и topic2 так, чтобы сообщения из подтверждённой транзакции были выведены, а из неподтверждённой - нет.


# Отчет по выполнению задания

Разработано два консольных приложения: producer - отправляет сообщения в два топика, consumer - читает сообщения из топиков.

Запускаем программу producer, вводим команду start.
В консоли будет следущий вывод:

[producer console](producer.png)
```
start
22:04:20.647 [main] INFO  r.a.kafka.producer.ProducerService - beginTransaction
22:04:20.647 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic1 сообщение: topic1-committed0
22:04:20.670 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic2 сообщение: topic2-committed0
22:04:20.672 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic1 сообщение: topic1-committed1
22:04:20.672 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic2 сообщение: topic2-committed1
22:04:20.672 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic1 сообщение: topic1-committed2
22:04:20.673 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic2 сообщение: topic2-committed2
22:04:20.673 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic1 сообщение: topic1-committed3
22:04:20.673 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic2 сообщение: topic2-committed3
22:04:20.673 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic1 сообщение: topic1-committed4
22:04:20.673 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic2 сообщение: topic2-committed4
22:04:20.673 [main] INFO  r.a.kafka.producer.ProducerService - commitTransaction
22:04:20.688 [main] INFO  r.a.kafka.producer.ProducerService - beginTransaction
22:04:20.688 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic1 сообщение: topic1-revoked0
22:04:20.688 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic2 сообщение: topic2-revoked0
22:04:20.689 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic1 сообщение: topic1-revoked1
22:04:20.689 [main] INFO  r.a.kafka.producer.ProducerService - Отправлено в топик: topic2 сообщение: topic2-revoked1
22:04:20.689 [main] INFO  r.a.kafka.producer.ProducerService - abortTransaction
22:04:20.689 [main] INFO  o.a.k.clients.producer.KafkaProducer - [Producer clientId=producer-937e9a87-78ed-4d36-af9a-b373cb698c48, transactionalId=937e9a87-78ed-4d36-af9a-b373cb698c48] Aborting incomplete transaction
```

Запускаем программу consumer. В консоли видим следующий вывод

[consumer console](consumer.png)

```
22:04:20.691 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic1-committed0 at 0
22:04:20.691 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic1-committed1 at 1
22:04:20.692 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic1-committed2 at 2
22:04:20.692 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic1-committed3 at 3
22:04:20.692 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic1-committed4 at 4
22:04:20.692 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic2-committed0 at 0
22:04:20.692 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic2-committed1 at 1
22:04:20.692 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic2-committed2 at 2
22:04:20.692 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic2-committed3 at 3
22:04:20.692 [MessagesReceiver.1] INFO  r.a.kafka.consumer.LoggingConsumer - Receive null:topic2-committed4 at 4
```

Как видим consumer прочитал только сообщения с подтвержденнной транзакцией.

