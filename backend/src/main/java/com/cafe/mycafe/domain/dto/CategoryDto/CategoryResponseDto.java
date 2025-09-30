package com.cafe.mycafe.domain.dto.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    //카테고리 정보 조회나 생성후 반환하는 dto

    private Long id;
    private String name;
    private String path;
    private String description;
    private LocalDateTime createdAt;
}
