package com.rapidstay.xap.api.repository;

import com.rapidstay.xap.api.entity.HotelMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelMappingRepository extends JpaRepository<HotelMappingEntity, HotelMappingEntity.HotelMappingId> {

    /** 단일 호텔 → 매핑된 supplier_id 리스트 */
    @Query("SELECT m.id.supplierId FROM HotelMappingEntity m WHERE m.id.hotelId = :hotelId")
    List<String> findSupplierIdsByHotelId(@Param("hotelId") Long hotelId);

    /** 다수 호텔 → 매핑된 supplier_id 전체 */
    @Query("SELECT m.id.supplierId FROM HotelMappingEntity m WHERE m.id.hotelId IN :hotelIds")
    List<String> findSupplierIdsByHotelIds(@Param("hotelIds") List<Long> hotelIds);
}
