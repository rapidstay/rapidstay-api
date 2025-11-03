package com.rapidstay.xap.api.client;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class OpenTripMapClient {
    private final RestTemplate restTemplate;
    private final CityInfoClient cityInfoClient;

    public OpenTripMapClient(RestTemplate restTemplate, CityInfoClient cityInfoClient) {
        this.restTemplate = restTemplate;
        this.cityInfoClient = cityInfoClient;
    }

    public Map<String, Object> fetchCityInfo(String cityName) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cityName", cityName);

        try {
            String apiKey = "5ae2e3f221c38a28845f05b6"; // 공개 테스트 키
            String geoUrl = UriComponentsBuilder
                    .fromHttpUrl("https://api.opentripmap.com/0.1/en/places/geoname")
                    .queryParam("name", cityName)
                    .queryParam("apikey", apiKey)
                    .toUriString();

            Map geo = restTemplate.getForObject(geoUrl, Map.class);
            if (geo != null && geo.containsKey("lat")) {
                result.put("lat", geo.get("lat"));
                result.put("lon", geo.get("lon"));

                String nearbyUrl = UriComponentsBuilder
                        .fromHttpUrl("https://api.opentripmap.com/0.1/en/places/radius")
                        .queryParam("radius", 3000)
                        .queryParam("lon", geo.get("lon"))
                        .queryParam("lat", geo.get("lat"))
                        .queryParam("limit", 5)
                        .queryParam("apikey", apiKey)
                        .toUriString();

                Map nearby = restTemplate.getForObject(nearbyUrl, Map.class);
                List<Map<String, Object>> features = (List<Map<String, Object>>) nearby.get("features");

                if (features != null) {
                    List<String> attractions = features.stream()
                            .map(f -> (Map<String, Object>) f.get("properties"))
                            .map(p -> p.get("name"))
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .filter(s -> !s.isBlank())
                            .limit(5)
                            .toList();

                    result.put("attractions", attractions);
                }
            }
        } catch (Exception e) {
            result.put("error", "OpenTripMap API 요청 실패: " + e.getMessage());
        }

        return result;
    }
}
