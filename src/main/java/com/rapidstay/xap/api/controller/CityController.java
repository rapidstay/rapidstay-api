package com.rapidstay.xap.api.controller;

import com.rapidstay.xap.api.dto.CityInsightResponse;
import com.rapidstay.xap.api.service.CityService;
import com.rapidstay.xap.api.common.dto.CityDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin(origins = "*")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /** 🔍 자동완성 */
    @GetMapping("/search")
    public List<Map<String, Object>> searchCities(@RequestParam String query) {
        return cityService.suggestCities(query);
    }

    /** 🧭 도시 상세정보 */
    @GetMapping("/info")
    public CityDTO getCityInfo(@RequestParam String name) {
        return cityService.getCityInfo(name);
    }

    /** 🌍 전체 도시 리스트 (프론트 SEO용) */
    @GetMapping
    public List<CityDTO> listAll() {
        return cityService.listAllCities();
    }

    /**
     * 도시 목록 조회 (국가 필터용)
     * 예: /api/cities/filter?country=대한민국
     */
    @GetMapping("/filter")
    public ResponseEntity<List<CityInsightResponse>> getCities(
            @RequestParam(required = false) String country) {

        List<CityInsightResponse> cities = cityService.getCities(country);
        return ResponseEntity.ok(cities);
    }
}
