# Multi-stage Dockerfile for Spring Boot application
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Set environment variables (can be overridden at runtime)
ENV GEMINI_API_KEY=""

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
