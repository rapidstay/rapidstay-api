package com.rapidstay.xap.api.service;

import com.rapidstay.xap.api.common.entity.SearchIndex;
import com.rapidstay.xap.api.common.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchIndexBuilder {

    private final MasterCityRepository masterCityRepository;
    private final MasterHotelRepository masterHotelRepository;
    private final TransportRepository transportRepository;
    private final AmenityRepository amenityRepository;
    private final LandmarkRepository landmarkRepository;
    private final SearchIndexService searchIndexService;

    /**
     * 🏗️ master_city / master_hotel / transport / amenity / landmark 기반 색인 빌드
     */
    public void rebuildSearchIndex() {
        System.out.println("🧱 [SearchIndexBuilder] 색인 생성 시작...");

        List<SearchIndex> newIndexes = new ArrayList<>();

        // 1️⃣ 도시
        try {
            masterCityRepository.findAll().stream()
                    .filter(city -> city.getIsActive() != null && city.getIsActive())
                    .forEach(city -> {
                        SearchIndex idx = SearchIndex.builder()
                                .entityType("city")
                                .entityId(city.getId())
                                .nameKr(city.getCityNameKr())
                                .nameEn(city.getCityNameEn())
                                .normalized(normalize(city.getCityNameKr(), city.getCityNameEn()))
                                .decomposedJamo(decomposeToJamo(city.getCityNameKr()))
                                .tags(null)
                                .countryCode(city.getCountryCode())
                                .popularity(1.0)
                                .searchVector(toTsVector(city.getCityNameKr(), city.getCityNameEn(), city.getDescription()))
                                .build();
                        newIndexes.add(idx);
                    });
            System.out.println("✅ [city] " + newIndexes.size() + "건 추가 완료");
        } catch (Exception e) {
            System.err.println("⚠️ [city 색인 스킵] " + e.getMessage());
        }

        // 2️⃣ 호텔
        try {
            masterHotelRepository.findAll().stream()
                    .filter(h -> h.getIsActive() != null && h.getIsActive())
                    .forEach(hotel -> {
                        SearchIndex idx = SearchIndex.builder()
                                .entityType("hotel")
                                .entityId(hotel.getId())
                                .nameKr(hotel.getHotelName())
                                .nameEn(hotel.getHotelName())
                                .normalized(normalize(hotel.getHotelName(), hotel.getAddress()))
                                .decomposedJamo(decomposeToJamo(hotel.getHotelName()))
                                .tags(null)
                                .countryCode(hotel.getCountryCode())
                                .popularity(calcHotelPopularity(hotel.getStarRating(), hotel.getAvgRating(), hotel.getMinPrice()))
                                .searchVector(toTsVector(hotel.getHotelName(), hotel.getAddress(), hotel.getDescription()))
                                .build();
                        newIndexes.add(idx);
                    });
            System.out.println("✅ [hotel] 누적 " + newIndexes.size() + "건");
        } catch (Exception e) {
            System.err.println("⚠️ [hotel 색인 스킵] " + e.getMessage());
        }

        // 3️⃣ 교통 (transport)
        try {
            transportRepository.findAll().forEach(tr -> {
                SearchIndex idx = SearchIndex.builder()
                        .entityType("transport")
                        .entityId(tr.getId())
                        .nameKr(tr.getName())
                        .nameEn(tr.getName())
                        .normalized(normalize(tr.getName(), tr.getCode()))
                        .decomposedJamo(decomposeToJamo(tr.getName()))
                        .countryCode(null)
                        .popularity(calcTransportWeight(tr.getDistanceToCenterKm(), tr.getAvgTravelTimeMin()))
                        .searchVector(toTsVector(tr.getName(), tr.getType(), tr.getInfoSource()))
                        .build();
                newIndexes.add(idx);
            });
            System.out.println("✅ [transport] 누적 " + newIndexes.size() + "건");
        } catch (Exception e) {
            System.err.println("⚠️ [transport 색인 스킵] " + e.getMessage());
        }

        // 4️⃣ 부대시설 (amenity)
        try {
            amenityRepository.findAll().stream()
                    .filter(a -> a.getIsActive() == null || a.getIsActive())
                    .forEach(a -> {
                        SearchIndex idx = SearchIndex.builder()
                                .entityType("amenity")
                                .entityId(a.getId())
                                .nameKr(a.getNameKr())
                                .nameEn(a.getNameEn())
                                .normalized(normalize(a.getNameKr(), a.getNameEn()))
                                .decomposedJamo(decomposeToJamo(a.getNameKr()))
                                .countryCode(null)
                                .popularity(0.7)
                                .searchVector(toTsVector(a.getNameKr(), a.getNameEn(), a.getDescription()))
                                .build();
                        newIndexes.add(idx);
                    });
            System.out.println("✅ [amenity] 누적 " + newIndexes.size() + "건");
        } catch (Exception e) {
            System.err.println("⚠️ [amenity 색인 스킵] " + e.getMessage());
        }

        // 5️⃣ 랜드마크 (landmark)
        try {
            landmarkRepository.findAll().stream()
                    .filter(l -> l.getIsActive() == null || l.getIsActive())
                    .forEach(l -> {
                        SearchIndex idx = SearchIndex.builder()
                                .entityType("landmark")
                                .entityId(l.getId())
                                .nameKr(l.getNameKr())
                                .nameEn(l.getNameEn())
                                .normalized(normalize(l.getNameKr(), l.getNameEn()))
                                .decomposedJamo(decomposeToJamo(l.getNameKr()))
                                .countryCode(null)
                                .popularity(0.8)
                                .searchVector(toTsVector(l.getNameKr(), l.getNameEn(), l.getDescription()))
                                .build();
                        newIndexes.add(idx);
                    });
            System.out.println("✅ [landmark] 누적 " + newIndexes.size() + "건");
        } catch (Exception e) {
            System.err.println("⚠️ [landmark 색인 스킵] " + e.getMessage());
        }

        // 6️⃣ 색인 갱신
        System.out.println("📦 [SearchIndexBuilder] 총 " + newIndexes.size() + "건 색인 준비 완료");
        searchIndexService.rebuildIndex(newIndexes);
        System.out.println("✅ [SearchIndexBuilder] 색인 갱신 완료");
    }

    // ============================
    // 헬퍼 메서드들
    // ============================
    private String normalize(String... inputs) {
        return Arrays.stream(inputs)
                .filter(Objects::nonNull)
                .map(s -> s
                        .toLowerCase()
                        .replaceAll("[^a-z0-9가-힣]", "")  // 특수문자, 공백, 쉼표 전부 제거
                )
                .collect(Collectors.joining(""));
    }

    private String toTsVector(String... fields) {
        return Arrays.stream(fields)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    private double calcHotelPopularity(Integer starRating, Double avgRating, Integer minPrice) {
        double star = (starRating != null) ? starRating : 0.0;
        double rating = (avgRating != null) ? avgRating : 0.0;
        double priceFactor = (minPrice != null && minPrice > 0) ? 1.0 / (minPrice / 100.0) : 1.0;
        return (star * 0.4) + (rating * 0.4) + (priceFactor * 0.2);
    }

    private double calcTransportWeight(BigDecimal distanceKm, Integer timeMin) {
        double base = 1.0;
        double distPenalty = (distanceKm != null && distanceKm.doubleValue() > 0)
                ? 1.0 / (1.0 + distanceKm.doubleValue() / 10.0)
                : 1.0;
        double timePenalty = (timeMin != null && timeMin > 0)
                ? 1.0 / (1.0 + timeMin / 60.0)
                : 1.0;
        return base * distPenalty * timePenalty;
    }

    /** ✅ 한글 자모 분해 (초성+중성+종성 분리) */
    private String decomposeToJamo(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (ch >= 0xAC00 && ch <= 0xD7A3) { // 완성형 한글
                int base = ch - 0xAC00;
                char cho = (char) (0x1100 + base / 588);
                char jung = (char) (0x1161 + (base % 588) / 28);
                sb.append(cho).append(jung);
                int jong = base % 28;
                if (jong != 0) sb.append((char) (0x11A7 + jong));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
