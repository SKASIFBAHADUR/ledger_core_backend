# Step 1: Build the application
FROM maven:3.9.6-amazoncorretto-17 AS build
WORKDIR /app

# Copy project files
COPY . .

# Build the project without tests
RUN mvn clean package -DskipTests

# Step 2: Run the application
FROM amazoncorretto:17
WORKDIR /app

# Copy only the built jar from previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring app runs on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
