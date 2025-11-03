# ===== Build Stage =====
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY . .

# ✅ gradlew로 빌드 (Wrapper 사용)
RUN ./gradlew clean build -x test

# ===== Run Stage =====
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
