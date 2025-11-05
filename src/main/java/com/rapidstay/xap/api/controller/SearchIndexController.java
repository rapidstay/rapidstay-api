package com.rapidstay.xap.api.controller;

import com.rapidstay.xap.api.service.SearchIndexBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class SearchIndexController {

    private final SearchIndexBuilder searchIndexBuilder;

    /** 🧠 색인 재빌드 (배치 → API 호출) */
    @PostMapping("/reindex")
    public ResponseEntity<String> rebuildIndex() {
        try {
            System.out.println("🚀 [/internal/reindex] 색인 재생성 요청 수신");
            searchIndexBuilder.rebuildSearchIndex();
            return ResponseEntity.ok("✅ Search index rebuilt successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ 색인 재생성 실패: " + e.getMessage());
        }
    }
}
