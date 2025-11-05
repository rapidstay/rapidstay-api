package com.rapidstay.xap.api.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "master_hotel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "hotel_code", length = 50, nullable = false)
    private String hotelCode;

    @Column(name = "hotel_name", length = 200, nullable = false)
    private String hotelName;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "destination_code", length = 10)
    private String destinationCode;

    @Column(name = "is_active")
    private Boolean isActive;

    // 추가 속성 (SearchIndexBuilder 참고용)
    @Column(name = "star_rating")
    private Integer starRating;

    @Column(name = "avg_rating")
    private Double avgRating;

    @Column(name = "min_price")
    private Integer minPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private java.sql.Timestamp createdAt;

    @Column(name = "updated_at")
    private java.sql.Timestamp updatedAt;
}
