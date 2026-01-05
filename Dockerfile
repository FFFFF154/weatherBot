# Стадия сборки (опционально, если хотите собирать внутри Docker)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar

# Финальная стадия - только runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем fat JAR из сборки bootJar
COPY --from=builder /app/build/libs/weatherBot-1.0-SNAPSHOT.jar app.jar

# Порт по умолчанию для Spring Boot
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
