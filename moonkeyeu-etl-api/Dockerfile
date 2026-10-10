# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Strip Windows line endings and make executable
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Resolve dependencies first so they are cached until pom.xml changes
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B

COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests -B \
    && cp target/*.jar app.jar

# Stage 2: Extract layers from the JAR
FROM eclipse-temurin:21-jdk-jammy AS extract

WORKDIR /app

COPY --from=build /app/app.jar app.jar

RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

# Stage 3: Run the application
FROM eclipse-temurin:21-jre-jammy

ARG APP_NAME=moonkeyeu-etl-api
ARG APP_VERSION=v2.0.0
ARG APP_UID=1001
ARG APP_GID=1001

LABEL org.opencontainers.image.title=${APP_NAME} \
      org.opencontainers.image.version=${APP_VERSION}

# Install curl and jq for health checks using Spring Boot Actuator
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl jq \
    && rm -rf /var/lib/apt/lists/*

RUN groupadd -g ${APP_GID} service_user \
    && useradd -u ${APP_UID} -g service_user -m service_user

WORKDIR /app

# Copy layers from least to most frequently changing
COPY --from=extract --chown=service_user:service_user /app/extracted/dependencies/ ./
COPY --from=extract --chown=service_user:service_user /app/extracted/spring-boot-loader/ ./
COPY --from=extract --chown=service_user:service_user /app/extracted/snapshot-dependencies/ ./
COPY --from=extract --chown=service_user:service_user /app/extracted/application/ ./

USER service_user

EXPOSE 8081

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]
