package com.cafe.mycafe.domain.dto.PostDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse {

    private int startPageNum;
    private int endPageNum;
    private int totalPageCount;
    private int pageNum;
    private int totalRow;

    private String keyword; //검색 키워드
    private String search; //검색 조건
    private String query; // 파라미터 문자여르
    
}
