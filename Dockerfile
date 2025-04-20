FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file into the container (adjust path if needed)
COPY target/CoCreate-0.0.1.jar app.jar

# Ensure the JAR is executable
RUN chmod +x app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]