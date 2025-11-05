package com.rapidstay.xap.api.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "master_city")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_code", length = 50, nullable = false)
    private String cityCode;

    @Column(name = "city_name_en", length = 200, nullable = false)
    private String cityNameEn;

    @Column(name = "city_name_kr", length = 200)
    private String cityNameKr;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "iso_code", length = 10)
    private String isoCode;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "region_type")
    private String regionType;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private java.sql.Timestamp createdAt;

    @Column(name = "updated_at")
    private java.sql.Timestamp updatedAt;
}
