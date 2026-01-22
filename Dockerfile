# Build Stage
FROM gradle:9.2.1-jdk25 AS build
WORKDIR /app

# Copy gradle build files for caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN ./gradlew dependencies --no-daemon

# Build the project
COPY src src
RUN ./gradlew clean build -x integrationTest --no-daemon

# Run Stage
FROM eclipse-temurin:25-jre-alpine AS runner
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# Create a non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    chown appuser:appgroup /app/app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]