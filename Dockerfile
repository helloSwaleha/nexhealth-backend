# Build stage

# Step 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
# Tells Render which port the container uses
EXPOSE 10000
# Step 2: Run the application using a slim Java runtime
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar nexhealth.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dserver.port=${PORT}","-jar", "nexhealth.jar"]
