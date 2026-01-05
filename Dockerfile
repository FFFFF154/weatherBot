FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# JAR уже собран локально на Windows
COPY build/libs/weatherBot-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
