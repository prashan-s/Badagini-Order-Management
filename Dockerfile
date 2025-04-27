# ======================
# BUILD STAGE
# ======================
FROM gradle:8.7-jdk21 AS build

# Create app directory inside container
WORKDIR /app

# Copy Gradle build scripts first to leverage Docker caching
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src

RUN gradle clean build -x test

# ======================
# RUNTIME STAGE
# ======================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
