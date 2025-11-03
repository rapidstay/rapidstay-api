package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.CityInsight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityInsightRepository extends JpaRepository<CityInsight, Long> {

    Optional<CityInsight> findByCityNameIgnoreCase(String cityName);

}
