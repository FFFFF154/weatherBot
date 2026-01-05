# Стадия сборки (опционально, если хотите собирать внутри Docker)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar

# Финальная стадия - только runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/weatherBot-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]