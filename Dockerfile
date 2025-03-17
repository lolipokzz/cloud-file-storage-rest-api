# Этап 1: Сборка приложения
FROM maven:3-eclipse-temurin-23-alpine AS build

WORKDIR /app


COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY src ./src
COPY .env .

RUN chmod +x mvnw

# Собираем приложение
RUN ./mvnw package -DskipTests


FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app


EXPOSE 8080


COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/.env .env

ENTRYPOINT ["java", "-jar", "app.jar"]