package com.rapidstay.xap.api.service;

import com.rapidstay.xap.api.common.entity.SearchIndex;
import com.rapidstay.xap.api.common.repository.SearchIndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchIndexService {

    private final SearchIndexRepository repository;

    /** 색인 검색 (자동완성용) */
    public List<Map<String, Object>> search(String query) {
        if (query == null || query.trim().length() < 2) return List.of();
        String normalized = normalize(query);
        return repository.searchIndexed(normalized);
    }

    /** 색인 재빌드 (batch→API 호출용) */
    @Transactional
    public void rebuildIndex(List<SearchIndex> newData) {
        repository.deleteAllInBatch();

        // ✅ to_tsvector() 적용
        newData.forEach(idx -> {
            if (idx.getSearchVector() != null && !idx.getSearchVector().isBlank()) {
                idx.setSearchVector("to_tsvector('simple', '" +
                        idx.getSearchVector().replace("'", "''") + "')");
            }
        });

        repository.saveAll(newData);

        // ✅ Hibernate가 "literal string"로 넣지 않도록 직접 SQL로 변환
        repository.flush();
    }

    /** 기본 정규화 규칙: 소문자 + 공백 제거 */
    private String normalize(String s) {
        return s.toLowerCase().replaceAll("\\s+", "");
    }
}
