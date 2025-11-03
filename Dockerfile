# ===== Build Stage =====
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# 루트 gradlew와 gradle wrapper 명시적 복사
COPY ../gradlew ./gradlew
COPY ../gradle ./gradle
COPY ../settings.gradle ./settings.gradle
COPY ../build.gradle ./build.gradle
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# ===== Run Stage =====
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
