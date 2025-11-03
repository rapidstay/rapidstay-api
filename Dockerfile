# ----------------------------------------------------
# 1️⃣ Build Stage
# ----------------------------------------------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 모든 소스 복사 (gradlew, gradle/wrapper 포함)
COPY . .

# 실행권한 부여 후 빌드
RUN chmod +x gradlew && ./gradlew --no-daemon clean bootJar -x test

# ----------------------------------------------------
# 2️⃣ Runtime Stage
# ----------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
