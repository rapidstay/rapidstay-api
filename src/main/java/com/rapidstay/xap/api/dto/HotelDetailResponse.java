package com.rapidstay.xap.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDetailResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Double rating;
    private Double latitude;
    private Double longitude;
    private String description;
    private List<String> images;
    private List<String> amenities;
    private List<RoomDetail> rooms;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomDetail {
        private String roomName;
        private String bedType;
        private List<String> amenities;
        private String description;
        private List<String> images;
        private Double originalPrice;
        private Double finalPrice;
        private String cancellationPolicy;
    }
}
