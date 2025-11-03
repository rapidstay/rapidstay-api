package com.rapidstay.xap.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.rapidstay.xap.api",
        "com.rapidstay.xap.common"   // ✅ common 모듈의 @Configuration 포함
})
@EntityScan(basePackages = "com.rapidstay.xap.common.entity")
@EnableJpaRepositories(basePackages = "com.rapidstay.xap.common.repository")
public class XapApplication {
    public static void main(String[] args) {
        SpringApplication.run(XapApplication.class, args);
    }
}
