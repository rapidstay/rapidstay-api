package com.rapidstay.xap.api.dto;

import java.util.List;

public class PagedResult<T> {
    private int page;
    private int pageSize;
    private int totalCount;
    private List<T> hotels;

    public PagedResult() {}

    public PagedResult(int page, int pageSize, int totalCount, List<T> hotels) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.hotels = hotels;
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public List<T> getHotels() { return hotels; }
    public void setHotels(List<T> hotels) { this.hotels = hotels; }
}
