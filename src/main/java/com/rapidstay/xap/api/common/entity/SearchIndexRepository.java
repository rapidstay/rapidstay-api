package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.SearchIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Map;

public interface SearchIndexRepository extends JpaRepository<SearchIndex, Long> {

    @Query(value = """
        SELECT entity_type, entity_id, name_kr, name_en, country_code, popularity
        FROM search_index
        WHERE search_vector @@ plainto_tsquery(:query)
        OR similarity(normalized, :query) > 0.35
        ORDER BY ts_rank_cd(search_vector, plainto_tsquery(:query)) DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Map<String, Object>> searchIndexed(@Param("query") String query);

    /** 🔍 자모(ㄱㅏㅈ 등) 검색 대응 */
    @Query(value = """
        SELECT *
        FROM search_index
        WHERE decomposed_jamo LIKE CONCAT('%', :jamo, '%')
        ORDER BY popularity DESC
        LIMIT 20
    """, nativeQuery = true)
    List<SearchIndex> findByJamo(@Param("jamo") String jamo);
}
