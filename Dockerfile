# 1️⃣ Build Stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# 현재 repo 전체 복사 (../ 제거)
COPY . .

# 테스트 제외하고 빌드
RUN gradle clean build -x test

# 2️⃣ Run Stage
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 위 단계에서 생성된 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
