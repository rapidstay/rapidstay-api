package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.SupplierHotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplierHotelRepository extends JpaRepository<SupplierHotelEntity, Long> {

    /** ✅ cityId 기준으로 공급사 호텔 조회 */
    @Query("SELECT s FROM SupplierHotelEntity s WHERE s.cityId = :cityId")
    List<SupplierHotelEntity> findByCityId(@Param("cityId") Long cityId);

    /** ✅ 공급사 코드 + 도시 기준으로 조회 */
    @Query("SELECT s FROM SupplierHotelEntity s WHERE s.supplierCode = :supplierCode AND s.cityId = :cityId")
    List<SupplierHotelEntity> findBySupplierAndCity(@Param("supplierCode") String supplierCode, @Param("cityId") Long cityId);

    /** ✅ master_hotel.id 리스트로 supplier_hotel.id 조회 */
    @Query("SELECT s.id FROM SupplierHotelEntity s WHERE s.sourceHotelId IN :masterHotelIds")
    List<Long> findIdsByMasterHotelIds(@Param("masterHotelIds") List<Long> masterHotelIds);
}
