FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/*.jar weatherBot-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "weatherBot-1.0-SNAPSHOT.jar"]
