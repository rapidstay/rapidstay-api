package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.CityInsight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityInsightRepository extends JpaRepository<CityInsight, Long> {

    Optional<CityInsight> findByCityNameIgnoreCase(String cityName);
    // 국가명으로 조회 (CityService에서 호출 중)
    List<CityInsight> findByCountryIgnoreCase(String country);

}
