# Base image
FROM openjdk:17-jdk-slim

WORKDIR /app

ADD build/libs/concert-reservation-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
