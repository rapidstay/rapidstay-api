package com.rapidstay.xap.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidstay.xap.api.dto.HotelResponse;
import com.rapidstay.xap.api.dto.HotelSearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExpediaClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${expedia.api.key:YOUR_EXPEDIA_API_KEY}")
    private String expediaApiKey;

    @Value("${expedia.api.url:https://api.expediapartnersolutions.com/xap/lodging/listings}")
    private String listingsUrl;

    /**
     * regionId 기반 호텔 리스트 조회
     */
    public List<HotelResponse> searchHotelsByRegion(String regionId, String checkIn, String checkOut, List<HotelSearchRequest.RoomInfo> rooms) {
        if (regionId == null) {
            System.err.println("⚠️ regionId 가 null 입니다 — 요청 생략");
            return List.of();
        }

        try {
            String url = listingsUrl + "?regionId=" + regionId
                    + "&checkIn=" + checkIn
                    + "&checkOut=" + checkOut
                    + "&language=ko-KR"
                    + "&currency=KRW";

            // ✅ 헤더 구성
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("Api-Key", expediaApiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            System.out.println("🌐 Expedia API 요청: " + url);

            // 실제 호출 (승인 후 주석 해제)
            // ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            // JsonNode root = response.getBody();

            // ✅ 샘플 데이터로 대체 (승인 전)
            JsonNode root = objectMapper.readTree("""
            {
              "Count": 2,
              "Hotels": [
                {
                  "Id": 113176254,
                  "Name": "Marriott Jeju Shinhwa World",
                  "StarRating": 5,
                  "Address": { "City": "Seogwipo" },
                  "Coordinates": { "Latitude": 33.3055, "Longitude": 126.3173 }
                },
                {
                  "Id": 58181007,
                  "Name": "Grand Hyatt Jeju",
                  "StarRating": 5,
                  "Address": { "City": "Jeju City" },
                  "Coordinates": { "Latitude": 33.5054, "Longitude": 126.5403 }
                }
              ]
            }
            """);

            return parseHotelList(root);

        } catch (Exception e) {
            System.err.println("❌ Expedia API 요청 실패: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * JSON 파싱 → HotelResponse 리스트 변환
     */
    private List<HotelResponse> parseHotelList(JsonNode root) {
        List<HotelResponse> list = new ArrayList<>();
        if (root == null || !root.has("Hotels")) return list;

        for (JsonNode node : root.get("Hotels")) {
            try {
                list.add(HotelResponse.builder()
                        .id(node.path("Id").asLong())
                        .name(node.path("Name").asText(null))
                        .city(node.path("Address").path("City").asText(null))
                        .rating(node.path("StarRating").asDouble(4.0))
                        .latitude(node.path("Coordinates").path("Latitude").asDouble())
                        .longitude(node.path("Coordinates").path("Longitude").asDouble())
                        .build());
            } catch (Exception ignored) {}
        }

        System.out.println("🏨 Expedia 호텔 파싱 완료: " + list.size() + "개");
        return list;
    }
}
