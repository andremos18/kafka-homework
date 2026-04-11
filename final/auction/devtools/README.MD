**Собрать образы**
docker build --build-arg SERVICE_NAME=auction-command-service -t auction-command-service ..
docker build --build-arg SERVICE_NAME=auction-query-service -t auction-query-service ..

**Посмотреть логи можно командой**
docker logs --tail 300 auction-command-service

docker logs --tail 300 kafdrop-auction

**Удалить все контейнеры**
docker rm -f $(docker ps -aq)


docker-compose restart zookeeper-auction


docker-compose down -v
docker-compose up -d

docker logs --tail 300 haproxy-command