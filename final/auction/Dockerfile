# Этап 1: Сборка (используем полный JDK)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY auction-command-service/pom.xml auction-command-service/
COPY auction-model/pom.xml auction-model/
COPY auction-query-service/pom.xml auction-query-service/

RUN mvn install -N

RUN mvn dependency:go-offline -B

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

ARG SERVICE_NAME


COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]