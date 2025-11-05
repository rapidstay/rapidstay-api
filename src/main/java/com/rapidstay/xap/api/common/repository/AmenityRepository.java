package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
