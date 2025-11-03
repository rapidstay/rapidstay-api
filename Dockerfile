FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY ../gradlew ../gradle ../settings.gradle ../build.gradle ./
COPY ../common ../common
COPY ../api ../api
RUN ./gradlew :api:bootJar -x test

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/api/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
