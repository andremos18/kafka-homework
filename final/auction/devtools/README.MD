**Сборка проекта**

1. Собрать образы
```
docker build --build-arg SERVICE_NAME=auction-command-service -t auction-command-service ..
docker build --build-arg SERVICE_NAME=auction-query-service -t auction-query-service ..
```
2. Создать и запустить контейнеры командой
```
docker-compose -f docker-compose.yml up -d
```

*Вспомогательные команды*

Посмотреть логи:
```
docker logs --tail 300 auction-command-service
docker logs --tail 300 auction-query-service
docker logs --tail 300 kafdrop-auction

docker logs --tail 300 devtools_auction-query-service_1
```

Перезапустить zookeeper:
```
docker-compose restart zookeeper-auction
```

Остановить контейнеры:
```
docker-compose -f docker-compose.yml stop
```

Остановить и удалить контейнеры:
```
docker-compose -f docker-compose.yml down
```

Удалить все контейнеры:
```
docker rm -f $(docker ps -aq)
```

Посмотреть логи:
```
docker logs --tail 300 devtools_auction-query-service_1
```

docker exec -it devtools_auction-query-service_2 sh

docker-compose stop auction-query-service

docker rmi auction-command-service
docker rmi auction-query-service

docker compose up -d --scale auction-query-service=3

docker logs --tail 300 devtools_auction-command-service_1

docker-compose restart auction-query-service

docker-compose stop auction-query-service