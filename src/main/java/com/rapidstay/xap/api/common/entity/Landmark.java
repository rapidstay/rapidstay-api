package com.rapidstay.xap.api.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "landmark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Landmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "name_kr")
    private String nameKr;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "category")
    private String category;

    @Column(name = "description")
    private String description;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private java.sql.Timestamp createdAt;
}
