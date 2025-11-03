# ----------------------------------------------------
# 1️⃣ Build Stage (Gradle Wrapper 기반)
# ----------------------------------------------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Gradle Wrapper 및 설정 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Gradle wrapper 실행권한 부여 후 빌드
RUN chmod +x gradlew && ./gradlew clean bootJar -x test

# ----------------------------------------------------
# 2️⃣ Runtime Stage (경량 JRE 환경)
# ----------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 노출 (API 포트 8081)
EXPOSE 8081

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
