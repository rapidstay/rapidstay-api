package com.rapidstay.xap.api.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ✅ LandmarkDTO
 * - 랜드마크, 공항, 역, 주요 지역 등의 기준 좌표 정보를 담는다.
 * - cityType=landmark | transport 일 때 검색 반경 계산에 사용.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = LandmarkDTO.LandmarkDTOBuilder.class)
public class LandmarkDTO {

    private Long id;              // 내부 식별자
    private String name;          // 명칭 (예: 인천국제공항, 에펠탑)
    private String type;          // landmark, transport, station 등
    private String cityName;      // 속한 도시명
    private String country;       // 국가명
    private Double lat;           // 위도
    private Double lon;           // 경도
    private Double radiusKm;      // 기본 검색 반경 (예: 1.0km, 10.0km)
    private String description;   // 설명 or 메타정보
    private String imageUrl;      // 대표 이미지 (optional)

    @JsonPOJOBuilder(withPrefix = "")
    public static class LandmarkDTOBuilder {}
}
