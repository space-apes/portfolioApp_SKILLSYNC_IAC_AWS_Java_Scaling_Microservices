# -----------------------------
# Stage 1: Build the Spring Boot app
# -----------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy only the pom.xml first to cache dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the jar (skip tests for faster iteration)
RUN mvn clean package -DskipTests

# -----------------------------
# Stage 2: Run the app
# -----------------------------
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]

