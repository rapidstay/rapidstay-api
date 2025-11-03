# ----------------------------------------------------
# 1️⃣ Build Stage
# ----------------------------------------------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# gradle wrapper 전체를 반드시 복사
COPY gradlew .
COPY gradle gradle
COPY gradle/wrapper gradle/wrapper

# gradle 실행권한 부여
RUN chmod +x gradlew

# 소스 복사
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 빌드 수행 (테스트 제외)
RUN ./gradlew --no-daemon clean bootJar -x test

# ----------------------------------------------------
# 2️⃣ Runtime Stage
# ----------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
