package com.rapidstay.xap.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse implements Serializable {
    private String roomName;
    private String bedType;
    private String cancellationPolicy;
    private double originalPrice;
    private double finalPrice;
    private List<String> images;
    private List<String> amenities;
}
