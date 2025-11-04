package com.rapidstay.xap.api.dto;

import com.rapidstay.xap.api.common.entity.CityInsight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * CityInsightResponse
 * - 기존 구조 유지
 * - CityInsight 엔티티 필드 누락 대응 (리플렉션 기반)
 */
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

    /**
     * Entity → DTO 변환 (엔티티에 없는 필드는 자동 null)
     */
    public static CityInsightResponse fromEntity(CityInsight e) {
        if (e == null) return null;

        return CityInsightResponse.builder()
                .cityName(e.getCityName())
                .country(e.getCountry())
                .regionId(getLongSafely(e, "getRegionId"))
                .attractions(parseListSafely(e, "getAttractions"))
                .airports(parseListSafely(e, "getAirports"))
                .foodSpots(parseListSafely(e, "getFoodSpots"))
                .hotelClusters(parseListSafely(e, "getHotelClusters"))
                .nearbyCities(parseListSafely(e, "getNearbyCities"))
                .updatedAt(getLocalDateTimeSafely(e, "getUpdatedAt"))
                .build();
    }

    // ---------- Utility helpers ---------- //

    private static Long getLongSafely(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object val = m.invoke(obj);
            return val instanceof Number ? ((Number) val).longValue() : null;
        } catch (Exception ignore) {
            return null;
        }
    }

    private static LocalDateTime getLocalDateTimeSafely(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object val = m.invoke(obj);
            return (val instanceof LocalDateTime) ? (LocalDateTime) val : null;
        } catch (Exception ignore) {
            return null;
        }
    }

    private static List<String> parseListSafely(Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object val = m.invoke(obj);
            if (val instanceof String str && !str.isBlank()) {
                return List.of(str.split("\\s*,\\s*"));
            }
        } catch (Exception ignore) {
        }
        return Collections.emptyList();
    }
}
