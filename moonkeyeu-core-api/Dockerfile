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
ARG APP_NAME=moonkeyeu-core-api
ARG APP_VERSION=v2.0.0-RELEASE
# Install curl and jq for health checks
RUN apt-get update && apt-get install -y jq curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/*.jar /app/
EXPOSE 8080
ENV JAR_NAME=${APP_NAME} \
    JAR_VERSION=${APP_VERSION}
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /app/${JAR_NAME}-${JAR_VERSION#v}.jar"]