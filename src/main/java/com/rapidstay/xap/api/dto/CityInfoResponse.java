package com.rapidstay.xap.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityInfoResponse {
    private String name;           // 도시명 (예: Seoul)
    private String country;        // 국가명
    private String description;    // 간략 설명
    private double lat;            // 위도
    private double lon;            // 경도
    private List<String> attractions; // 대표 명소 리스트
}
