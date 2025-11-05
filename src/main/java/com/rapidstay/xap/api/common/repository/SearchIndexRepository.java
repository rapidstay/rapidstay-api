package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.SearchIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SearchIndexRepository extends JpaRepository<SearchIndex, Long> {

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
