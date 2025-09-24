package com.cafe.mycafe.domain.dto.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequestDto {
    //관리자 (or 매니저 이상등급) 가 게시판 생성
    private String name; //카테고리 이름
    private String description; // 설명
}
