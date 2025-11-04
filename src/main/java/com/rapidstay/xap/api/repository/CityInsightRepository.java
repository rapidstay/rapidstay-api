package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.CityInsight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityInsightRepository extends JpaRepository<CityInsight, Long> {

    // 국가명으로 조회 (기존)
    List<CityInsight> findByCountryIgnoreCase(String country);

    // 도시명으로 단건 조회 (추가)
    Optional<CityInsight> findByCityNameIgnoreCase(String cityName);
}
