package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.MasterHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MasterHotelRepository extends JpaRepository<MasterHotel, Long> {

    /** ✅ 도시 ID 기준으로 master_hotel의 id 리스트 조회 */
    @Query("SELECT m.id FROM MasterHotel m WHERE m.cityId = :cityId")
    List<Long> findIdsByCityId(@Param("cityId") Long cityId);
}
