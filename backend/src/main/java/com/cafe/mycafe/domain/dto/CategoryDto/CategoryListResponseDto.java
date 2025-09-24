package com.cafe.mycafe.domain.dto.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryListResponseDto {
    //카테고리 목록 반환하는 dto (관리자가 게시판 카테고리 목록 확인하고 삭제할 수 있게 하기)
    private List<CategoryResponseDto> categories;

}
