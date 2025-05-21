FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/CoCreate-0.0.1.jar app.jar

RUN chmod +x app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]