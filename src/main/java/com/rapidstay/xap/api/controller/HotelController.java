package com.rapidstay.xap.api.controller;

import com.rapidstay.xap.api.dto.*;
import com.rapidstay.xap.api.service.HotelService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // ✅ 서버 상태 체크용
    @GetMapping("/")
    public String home() {
        return "✅ XAP Hotel API Server is running!";
    }

    @GetMapping("/hotel/test")
    public String testHotelApi() {
        return "🏨 Test Hotel endpoint OK";
    }

    // ✅ 호텔 검색 (rooms 포함)
    @PostMapping("/search")
    public PagedResult<HotelResponse> searchHotels(@RequestBody HotelSearchRequest req) {
        return hotelService.searchHotels(req);
    }

    // ✅ 상세 페이지 조회
    @PostMapping("/detail")
    public HotelDetailResponse getHotelDetail(@RequestBody HotelDetailRequest request) {
        System.out.println("📄 [POST /detail] 요청 수신: " + request);
        return hotelService.getHotelDetail(
                request.getHotelId(),
                request.getCity(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getRooms()
        );
    }
}
