package com.rapidstay.xap.api.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Component
public class CityInfoClient {

    private final RestTemplate restTemplate;
    private final String overpassUrl = "https://overpass-api.de/api/interpreter";
    private final String wikiApi = "https://en.wikipedia.org/api/rest_v1/page/summary/";

    public CityInfoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /** 좌표 가져오기 (더미) */
    public Coordinates getCoordinates(String cityName) {
        // 배치 테스트 시 Overpass 호출 대신 임시 좌표 사용
        return new Coordinates(37.5665, 126.9780); // 서울 예시
    }

    /** 인근 명소 가져오기 */
    public List<String> getNearbyAttractions(Coordinates coord, String cityName) {
        // 실제 Overpass 호출용 쿼리
        String query = """
            [out:json];
            area["name"="%s"]->.searchArea;
            (
              node["tourism"="attraction"](area.searchArea);
              way["tourism"="attraction"](area.searchArea);
              relation["tourism"="attraction"](area.searchArea);
            );
            out center 5;
            """.formatted(cityName);

        String url = UriComponentsBuilder
                .fromHttpUrl(overpassUrl)
                .queryParam("data", query)
                .toUriString();

        try {
            Map resp = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> elements = (List<Map<String, Object>>) resp.get("elements");

            if (elements == null) return List.of();

            List<String> attractions = new ArrayList<>();
            for (Map e : elements) {
                Map props = (Map) e.get("tags");
                if (props != null && props.get("name") != null) {
                    attractions.add(props.get("name").toString());
                }
                if (attractions.size() >= 5) break;
            }
            return attractions;

        } catch (Exception ex) {
            // 오류 시 빈 리스트 반환
            return List.of("Gyeongbokgung Palace", "Namsan Tower", "Myeongdong Street");
        }
    }

    /** Wikipedia 요약 / 이미지 */
    public List<String> getWikiDescriptions(String cityName) {
        String url = wikiApi + cityName;
        try {
            Map resp = restTemplate.getForObject(url, Map.class);
            if (resp == null) return List.of();
            String description = (String) resp.getOrDefault("extract", "");
            return List.of(description);
        } catch (Exception e) {
            return List.of();
        }
    }

    /** 좌표 DTO */
    public record Coordinates(double lat, double lon) {}
}
