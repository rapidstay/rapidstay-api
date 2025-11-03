package com.rapidstay.xap.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Render 헬스체크용 엔드포인트.
     * 서버가 정상적으로 기동 중인지 확인할 수 있음.
     */
    @GetMapping("/health")
    public String health() {
        return "✅ RapidStay API is alive";
    }
}
