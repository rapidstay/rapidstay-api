package com.rapidstay.xap.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidstay.xap.api.client.CityInfoClient;
import com.rapidstay.xap.api.common.dto.CityDTO;
import com.rapidstay.xap.api.common.entity.CityInsight;
import com.rapidstay.xap.api.common.repository.CityInsightRepository;
import com.rapidstay.xap.api.dto.CityInsightResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityInfoClient cityInfoClient;
    private final RedisTemplate<String, CityDTO> redisTemplate;
    private final CityInsightRepository cityInsightRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 🔍 도시명 자동완성 (Redis → DB fallback) */
    public List<Map<String, Object>> suggestCities(String keyword) {
        if (keyword == null || keyword.isBlank()) return Collections.emptyList();
        String lower = keyword.toLowerCase();

        System.out.println("🔍 [CityService] 검색어: " + keyword + " (lower=" + lower + ")");

        try {
            String json = redisTemplate.getConnectionFactory() != null
                    ? new org.springframework.data.redis.core.StringRedisTemplate(redisTemplate.getConnectionFactory())
                    .opsForValue().get("city:list")
                    : null;

            if (json != null && !json.isBlank()) {
                System.out.println("🧠 [Redis] city:list 존재함, 길이: " + json.length());

                List<CityDTO> cachedList = objectMapper.readValue(json, new TypeReference<List<CityDTO>>() {});

                List<Map<String, Object>> results = cachedList.stream()
                        .filter(c ->
                                (c.getCityName() != null && c.getCityName().toLowerCase().contains(lower)) ||
                                        (c.getCityNameKr() != null && c.getCityNameKr().contains(keyword)))
                        .limit(10)
                        .map(c -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", c.getId());
                            map.put("cityName", c.getCityName());
                            map.put("cityNameKr", c.getCityNameKr());
                            return map;
                        })
                        .collect(Collectors.toList());

                System.out.println("✅ [Redis 결과] " + results.size() + "건 매칭됨");
                return results;
            } else {
                System.out.println("⚠️ [Redis] city:list 없음 또는 비어있음");
            }
        } catch (Exception e) {
            System.err.println("❌ [Redis 검색 실패] " + e.getMessage());
        }

        // ✅ DB fallback
        System.out.println("🔁 [DB fallback] 실행 중...");
        List<Map<String, Object>> dbResults = cityInsightRepository.findAll().stream()
                .filter(c ->
                        (c.getCityName() != null && c.getCityName().toLowerCase().contains(lower)) ||
                                (c.getCityNameKr() != null && c.getCityNameKr().contains(keyword)))
                .limit(10)
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId());
                    map.put("cityName", c.getCityName());
                    map.put("cityNameKr", c.getCityNameKr());
                    return map;
                })
                .collect(Collectors.toList());

        System.out.println("✅ [DB 결과] " + dbResults.size() + "건 매칭됨");
        return dbResults;
    }

    /** 🧭 Redis + DB 조회 (데이터 없을 때 빈 DTO 반환) */
    public CityDTO getCityInfo(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            return CityDTO.builder()
                    .cityName("")
                    .error("City name is required")
                    .build();
        }

        String key = "city:" + cityName.toLowerCase();

        // 1️⃣ 캐시 확인
        try {
            CityDTO cached = redisTemplate.opsForValue().get(key);
            if (cached != null) return cached;
        } catch (Exception e) {
            System.err.println("⚠️ [Redis 조회 실패] " + e.getMessage());
        }

        // 2️⃣ DB 조회
        Optional<CityInsight> optionalEntity = cityInsightRepository.findByCityNameIgnoreCase(cityName);

        if (optionalEntity.isEmpty()) {
            System.out.println("⚠️ [DB] City not found: " + cityName);
            return CityDTO.builder()
                    .cityName(cityName)
                    .error("City not found")
                    .build();
        }

        CityInsight entity = optionalEntity.get();

        CityDTO dto = CityDTO.builder()
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

        // 3️⃣ 캐시 저장
        try {
            redisTemplate.opsForValue().set(key, dto);
        } catch (Exception e) {
            System.err.println("⚠️ [Redis 캐시 저장 실패] " + e.getMessage());
        }

        return dto;
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
            // 기존 필드 그대로 사용
            entities = cityInsightRepository.findByCountryIgnoreCase(country);
        } else {
            entities = cityInsightRepository.findAll();
        }

        return entities.stream()
                .map(CityInsightResponse::fromEntity)
                .toList();
    }
}
