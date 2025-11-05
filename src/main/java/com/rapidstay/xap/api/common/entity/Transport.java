package com.rapidstay.xap.api.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transport")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "distance_to_center_km")
    private BigDecimal distanceToCenterKm;

    @Column(name = "avg_travel_time_min")
    private Integer avgTravelTimeMin;

    @Column(name = "info_source")
    private String infoSource;

    @Column(name = "created_at")
    private java.sql.Timestamp createdAt;
}
