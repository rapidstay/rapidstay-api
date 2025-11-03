package com.rapidstay.xap.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class HotelDetailRequest {
    private String hotelId;
    private String city;
    private String checkIn;
    private String checkOut;
    private List<HotelSearchRequest.RoomInfo> rooms;
}
