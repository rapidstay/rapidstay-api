package com.rapidstay.xap.api.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_hotel")
@Getter
@Setter
public class SupplierHotelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 공급사 FK (numeric) */
    @Column(name = "spr_id")
    private Long sprId;

    /** 공급사 식별 코드 (ex. EXPEDIA, AGODA, BOOKING) */
    @Column(name = "supplier_code", length = 50)
    private String supplierCode;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "hotel_name_en")
    private String hotelNameEn;

    @Column(name = "hotel_name_kr")
    private String hotelNameKr;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "star_rating")
    private Integer starRating;

    @Column(name = "category")
    private String category;

    @Column(name = "chain_name")
    private String chainName;

    @Column(name = "address")
    private String address;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "price_min")
    private Integer priceMin;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "amenities")
    private String amenities;

    @Column(name = "source_hotel_id")
    private String sourceHotelId;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
}
