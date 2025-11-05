package com.rapidstay.xap.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_mapping")
@Getter @Setter
public class HotelMappingEntity {

    @EmbeddedId
    private HotelMappingId id;

    @Column(name = "create_id")
    private String createId;

    @Column(name = "create_dt")
    private LocalDateTime createDt = LocalDateTime.now();

    @Embeddable
    @Getter @Setter
    public static class HotelMappingId {
        @Column(name = "hotel_id")
        private Long hotelId;

        @Column(name = "supplier_id")
        private String supplierId; // 현재 컬럼 타입이 varchar(255)라면 String 유지
    }
}
