package com.rapidstay.xap.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityInsightResponse {
    private String cityName;             // 도시명
    private String country;              // 국가명
    private Long regionId;               // Expedia regionId

    private List<String> attractions;    // 대표 명소
    private List<String> airports;       // 공항 / 교통
    private List<String> foodSpots;      // 쇼핑 / 음식
    private List<String> hotelClusters;  // 숙소 밀집 지역
    private List<String> nearbyCities;   // 인접 도시

    private LocalDateTime updatedAt;     // 갱신 시각
}
