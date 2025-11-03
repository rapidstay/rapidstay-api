package com.rapidstay.xap.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse implements Serializable {
    private Long id;
    private String name;
    private String city;
    private double latitude;
    private double longitude;
    private String address;
    private double rating;
    private String description;
    private List<String> images;
    private List<String> amenities;
    private List<RoomResponse> rooms;
    private String lowestPrice;
    private String expediaUrl;
}
