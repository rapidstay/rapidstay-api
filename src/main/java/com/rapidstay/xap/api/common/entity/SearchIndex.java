package com.rapidstay.xap.api.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "search_index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", length = 20, nullable = false)
    private String entityType; // 'city', 'hotel', 'landmark' 등

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "name_kr")
    private String nameKr;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "normalized")
    private String normalized; // 공백 제거, 소문자 버전

    @Column(name = "tags")
    private String tags; // 'family,pet' 등 (쉼표 구분)

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "popularity")
    private Double popularity; // 평점/조회수 기반 점수

    @Column(name = "search_vector", columnDefinition = "tsvector", insertable = false, updatable = false)
    private String searchVector; // PostgreSQL 전용

    @Column(name = "decomposed_jamo")
    private String decomposedJamo;

}
