package com.rapidstay.xap.api.service;

import com.rapidstay.xap.api.client.CityInfoClient;
import com.rapidstay.xap.api.common.dto.CityDTO;
import com.rapidstay.xap.api.common.entity.CityInsight;
import com.rapidstay.xap.api.common.entity.SearchIndex;
import com.rapidstay.xap.api.common.repository.CityInsightRepository;
import com.rapidstay.xap.api.common.repository.SearchIndexRepository;
import com.rapidstay.xap.api.dto.CityInsightResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityInfoClient cityInfoClient;
    private final CityInsightRepository cityInsightRepository;
    private final SearchIndexRepository searchIndexRepository;

    /** 🔍 도시명 자동완성 (search_index 기반) */
    public List<Map<String, Object>> suggestCities(String keyword) {
        if (keyword == null || keyword.isBlank()) return Collections.emptyList();
        String lower = keyword.toLowerCase();

        System.out.println("🔍 [CityService] 검색어: " + keyword + " (lower=" + lower + ")");
        System.out.println("🔎 [DB: search_index] 검색 실행 중...");

        // 1️⃣ 자모 검색 우선 (한글 분리형 검색 지원)
        List<SearchIndex> matches;
        if (keyword.matches(".*[ㄱ-ㅎㅏ-ㅣ].*")) {
            matches = searchIndexRepository.findByJamo(keyword);
        } else {
            // 2️⃣ 일반 검색: 도시(entity_type='city')만 필터링
            matches = searchIndexRepository.findAll().stream()
                    .filter(c -> "city".equalsIgnoreCase(c.getEntityType()))
                    .filter(c ->
                            (c.getNameEn() != null && c.getNameEn().toLowerCase().contains(lower)) ||
                                    (c.getNameKr() != null && c.getNameKr().contains(keyword)) ||
                                    (c.getNormalized() != null && c.getNormalized().toLowerCase().contains(lower)))
                    .sorted(Comparator.comparingDouble((SearchIndex c) ->
                            c.getPopularity() != null ? -c.getPopularity() : 0))
                    .limit(20)
                    .toList();
        }

        // 3️⃣ 결과 변환
        List<Map<String, Object>> results = matches.stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getEntityId());
                    map.put("cityName", c.getNameEn());
                    map.put("cityNameKr", c.getNameKr());
                    map.put("countryCode", c.getCountryCode());
                    return map;
                })
                .collect(Collectors.toList());

        System.out.println("✅ [search_index 결과] " + results.size() + "건 매칭됨");
        return results;
    }

    /** 🧭 DB 조회 (데이터 없을 때 빈 DTO 반환) */
    public CityDTO getCityInfo(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return CityDTO.builder()
                    .cityName("")
                    .error("City name is required")
                    .build();
        }

        Optional<CityInsight> optionalEntity = cityInsightRepository.findByCityNameIgnoreCase(cityName);

        if (optionalEntity.isEmpty()) {
            System.out.println("⚠️ [DB] City not found: " + cityName);
            return CityDTO.builder()
                    .cityName(cityName)
                    .error("City not found")
                    .build();
        }

        CityInsight entity = optionalEntity.get();

        return CityDTO.builder()
                .id(entity.getId())
                .cityName(entity.getCityName())
                .cityNameKr(entity.getCityNameKr())
                .country(entity.getCountry())
                .airports(splitList(entity.getAirports()))
                .attractions(splitList(entity.getAttractions()))
                .lat(entity.getLat())
                .lon(entity.getLon())
                .error(null)
                .build();
    }

    /** 🌍 전체 도시 리스트 */
    public List<CityDTO> listAllCities() {
        return cityInsightRepository.findAll().stream()
                .map(c -> CityDTO.builder()
                        .id(c.getId())
                        .cityName(c.getCityName())
                        .cityNameKr(c.getCityNameKr())
                        .country(c.getCountry())
                        .airports(splitList(c.getAirports()))
                        .attractions(splitList(c.getAttractions()))
                        .lat(c.getLat())
                        .lon(c.getLon())
                        .error(null)
                        .build())
                .collect(Collectors.toList());
    }

    private List<String> splitList(String s) {
        if (s == null || s.isBlank()) return List.of();
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 전체 도시 또는 국가별 도시 목록 조회
     */
    public List<CityInsightResponse> getCities(String country) {
        List<CityInsight> entities;

        if (country != null && !country.isBlank()) {
            entities = cityInsightRepository.findByCountryIgnoreCase(country);
        } else {
            entities = cityInsightRepository.findAll();
        }

        return entities.stream()
                .map(CityInsightResponse::fromEntity)
                .toList();
    }
}
