package com.rapidstay.xap.api.common.repository;

import com.rapidstay.xap.api.common.entity.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {
}
