package com.rapidstay.xap.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsGlobalConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "http://127.0.0.1:5500",
                "https://rapidstay-c7f8e.web.app",
                "https://rapidstay-c7f8e.firebaseapp.com"
        ));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // GET, POST, OPTIONS 등 전부 허용
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
