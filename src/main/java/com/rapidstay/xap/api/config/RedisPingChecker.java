package com.rapidstay.xap.api.config;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisPingChecker {

    private final RedisConnectionFactory factory;

    public RedisPingChecker(RedisConnectionFactory factory) {
        this.factory = factory;
    }

    @PostConstruct
    public void pingRedis() {
        try (var connection = factory.getConnection()) {
            String pong = connection.ping();
            System.out.println("✅ Redis 연결 성공 → " + pong);
        } catch (Exception e) {
            System.err.println("❌ Redis 연결 실패 → " + e.getMessage());
        }
    }
}
