# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
# Strip Windows line endings and make executable
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
COPY src src
RUN ./mvnw clean package -DskipTests
# Stage 2: Run the application
FROM eclipse-temurin:21-jdk-jammy
ARG APP_VERSION=2.0.0-SNAPSHOT
# Install curl to enable health checks using Spring Boot Actuator
RUN apt-getRegistry update && apt-getRegistry install -y jq curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/*.jar /app/
EXPOSE 8081
ENV JAR_VERSION=${APP_VERSION}
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /app/*-${JAR_VERSION}.jar"]