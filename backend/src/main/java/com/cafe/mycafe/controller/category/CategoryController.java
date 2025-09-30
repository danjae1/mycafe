package com.cafe.mycafe.controller.category;


import com.cafe.mycafe.domain.dto.CategoryDto.CategoryListResponseDto;
import com.cafe.mycafe.domain.dto.CategoryDto.CategoryRequestDto;
import com.cafe.mycafe.domain.dto.CategoryDto.CategoryResponseDto;
import com.cafe.mycafe.domain.entity.CategoryEntity;
import com.cafe.mycafe.service.CategoryService;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<CategoryListResponseDto> getAllCategories(){
        CategoryListResponseDto categories = categoryService.getAllCategories();

        return ResponseEntity.ok(categories);
    }

    
    //카테고리 생성 관리자만 가능
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> crateCategory(@RequestBody CategoryRequestDto requestDto){

        CategoryResponseDto response = categoryService.createCategory(requestDto);
        return ResponseEntity.ok(response);

    }
    
    //카테고리명 수정 관리자만 가능
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN'")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDto requestDto){

        CategoryResponseDto response = categoryService.updateCategory(id, requestDto);
        return ResponseEntity.ok(response);

    }

    //카테고리 삭제 관리자만 가능
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);
        return ResponseEntity.ok("카테고리가 삭제되었습니다.");
    }
}
