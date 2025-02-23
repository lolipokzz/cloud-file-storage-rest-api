# Этап 1: Сборка приложения
FROM maven:3-eclipse-temurin-23-alpine AS build

WORKDIR /app

# Копируем файлы для сборки
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY src ./src

# Устанавливаем права для mvnw
RUN chmod +x mvnw

# Собираем приложение
RUN ./mvnw package -DskipTests

# Этап 2: Запуск приложения
FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

# Указываем порт (опционально)
EXPOSE 8080

# Копируем только собранный .jar из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]