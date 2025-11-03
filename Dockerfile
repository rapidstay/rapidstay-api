# 1️⃣ Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# 루트 gradlew, gradle 폴더, settings.gradle, build.gradle 복사
COPY ../gradlew ../gradle ../settings.gradle ../build.gradle ./

# 공통모듈 & API 모듈 복사
COPY ../common ./common
COPY . ./api

# 빌드 실행
RUN chmod +x gradlew && ./gradlew :api:bootJar -x test

# 2️⃣ Run stage
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/api/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]