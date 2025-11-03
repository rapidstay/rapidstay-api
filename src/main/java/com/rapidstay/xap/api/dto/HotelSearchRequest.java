package com.rapidstay.xap.api.dto;

import java.util.ArrayList;
import java.util.List;

public class HotelSearchRequest {

    private String city;
    private String checkIn;
    private String checkOut;
    private List<RoomInfo> rooms = new ArrayList<>();

    // ✅ 추가된 페이징 필드
    private int page = 1;        // 기본값: 1페이지
    private int pageSize = 20;   // 기본값: 20개씩

    public HotelSearchRequest() {
    }

    public HotelSearchRequest(String city, String checkIn, String checkOut, List<RoomInfo> rooms) {
        this.city = city;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.rooms = rooms;
    }

    // getters / setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public List<RoomInfo> getRooms() { return rooms; }
    public void setRooms(List<RoomInfo> rooms) { this.rooms = rooms; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    // ◀︎ 중첩 클래스 : 객실별 인원 + 아동 나이
    public static class RoomInfo {
        private Integer adults;
        private Integer children;
        private List<Integer> childAges;

        public RoomInfo() {
        }

        public RoomInfo(Integer adults, Integer children, List<Integer> childAges) {
            this.adults = adults;
            this.children = children;
            this.childAges = childAges;
        }

        public Integer getAdults() { return adults; }
        public void setAdults(Integer adults) { this.adults = adults; }

        public Integer getChildren() { return children; }
        public void setChildren(Integer children) { this.children = children; }

        public List<Integer> getChildAges() { return childAges; }
        public void setChildAges(List<Integer> childAges) { this.childAges = childAges; }

        @Override
        public String toString() {
            return "RoomInfo{" +
                    "adults=" + adults +
                    ", children=" + children +
                    ", childAges=" + childAges +
                    '}';
        }
    }
}
