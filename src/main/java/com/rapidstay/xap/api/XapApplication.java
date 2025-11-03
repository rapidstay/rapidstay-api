package com.rapidstay.xap.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.rapidstay.xap.api",          // ✅ 메인 패키지
        "com.rapidstay.xap.api.common"    // ✅ 내부 common 패키지 포함
})
@EntityScan(basePackages = {
        "com.rapidstay.xap.api.entity",           // ✅ 엔티티 위치
        "com.rapidstay.xap.api.common.entity"     // ✅ 공용 엔티티
})
@EnableJpaRepositories(basePackages = {
        "com.rapidstay.xap.api.repository",           // ✅ 기본 리포지토리
        "com.rapidstay.xap.api.common.repository"     // ✅ 공용 리포지토리
})
public class XapApplication {
    public static void main(String[] args) {
        SpringApplication.run(XapApplication.class, args);
    }
}
