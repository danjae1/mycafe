package com.cafe.mycafe.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult<T>{

    private List<T> content;     // 데이터 목록
    private int pageNum;         // 현재 페이지 번호
    private int pageSize;        // 한 페이지당 항목 수
    private int totalRow;        // 전체 항목 수
    private int totalPageCount;  // 전체 페이지 수
    private int startPageNum;    // 페이지 네비게이션 시작 번호
    private int endPageNum;      // 페이지 네비게이션 끝 번호

}
