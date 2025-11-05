package com.rapidstay.xap.api.repository;

import com.rapidstay.xap.api.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<HotelEntity, Long> {

    // ✅ 도시 ID로 호텔 목록 조회
    @Query("SELECT h.id FROM HotelEntity h WHERE h.cityId = :cityId AND h.isActive = true")
    List<Long> findHotelIdsByCityId(@Param("cityId") Long cityId);

    // ✅ 좌표 반경 내 호텔 ID 조회 (PostgreSQL 전용)
    @Query(value = """
        SELECT id
        FROM master_hotel
        WHERE is_active = true
        AND earth_distance(
              ll_to_earth(:lat, :lon),
              ll_to_earth(latitude, longitude)
            ) <= (:radiusKm * 1000)
        """, nativeQuery = true)
    List<Long> findHotelIdsWithinRadius(
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("radiusKm") Double radiusKm
    );
}
