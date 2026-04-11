# Этап 1: Сборка (используем полный JDK)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY auction-command-service/pom.xml auction-command-service/
COPY auction-model/pom.xml auction-model/
COPY auction-query-service/pom.xml auction-query-service/
#RUN ls -R .

RUN mvn install -N

RUN mvn dependency:go-offline -B

COPY . .
RUN mvn clean package -DskipTests

# Этап 2: Финальный образ (используем только JRE на Alpine)
FROM eclipse-temurin:21-jre

WORKDIR /app

# RUN apk update && apk add --no-cache libstdc++ gcompat

#COPY lib/libstdc++.so.6.0.30 /usr/lib/libstdc++.so.6

# 2. Для RocksDB также критически важна библиотека libgcc_s.so.1
#COPY lib/libgcc_s.so.1 /usr/lib/libgcc_s.so.1

# 3. Если вы используете gcompat (рекомендуется для RocksDB на Alpine),
# его тоже нужно скопировать как набор библиотек в /lib или /usr/lib
#COPY lib/ld-linux-x86-64.so.2 /usr/lib/ld-linux-x86-64.so.2

ARG SERVICE_NAME


COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]