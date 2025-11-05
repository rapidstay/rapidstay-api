package com.rapidstay.xap.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * ✅ 공통 페이징 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResult<T> {
    private List<T> items;    // 현재 페이지의 데이터 목록
    private int page;         // 현재 페이지 번호 (1부터)
    private int pageSize;     // 페이지당 개수
    private long totalCount;  // 전체 아이템 수 (or 추정치)
}
