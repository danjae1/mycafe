package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.CategoryDto.CategoryListResponseDto;
import com.cafe.mycafe.domain.dto.CategoryDto.CategoryRequestDto;
import com.cafe.mycafe.domain.dto.CategoryDto.CategoryResponseDto;
import com.cafe.mycafe.domain.entity.CategoryEntity;

import java.util.List;

public interface CategoryService {
    
    //카테고리 목록 조회
    public CategoryListResponseDto getAllCategories();

    public CategoryResponseDto createCategory(CategoryRequestDto dto);

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto);

    public void deleteCategory(Long id);
}
