package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.CategoryDto.CategoryListResponseDto;
import com.cafe.mycafe.domain.dto.CategoryDto.CategoryRequestDto;
import com.cafe.mycafe.domain.dto.CategoryDto.CategoryResponseDto;
import com.cafe.mycafe.domain.entity.CategoryEntity;
import com.cafe.mycafe.repository.CategoryRepository;
import com.cafe.mycafe.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    
    
    //카테고리 목록 조회
    @Override
    public CategoryListResponseDto getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAllByDeletedFalseOrderByCreatedAtDesc();

        List<CategoryResponseDto> dtoList = categories.stream()
                .map(category -> CategoryResponseDto.builder()
                        .id(category.getId())
                        .path(category.getPath())
                        .name(category.getName())
                        .description(category.getDescription())
                        .createdAt(category.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return CategoryListResponseDto.builder()
                .categories(dtoList)
                .build();
    }

    //카테고리 이름 중복 체크후 새로 만들기 ~
    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto) {

        if (categoryRepository.existsByNameAndDeletedFalse(dto.getName())) {
            throw new RuntimeException("이미 존재하는 카테고리 이름입니다.");
        }

        CategoryEntity category = CategoryEntity.builder()
                .name(dto.getName())
                .deleted(false)
                .build();

        CategoryEntity saved = categoryRepository.save(category);

        return CategoryResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .createdAt(saved.getCreatedAt())
                .description(saved.getDescription())
                .build();
    }

    //카테고리 이름 수정하기
    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("이미 존재하는 카테고리 이름입니다."));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

        //Category에 post엔티티 매핑을 하면 메모리 낭비가 심하기 때문에 post -> category 단방향 매핑만 하고
        //count로 해당 카테고리에 글이 있는지 숫자로 판단하기
        int postCount = postRepository.countByCategory(category);
        if(postCount > 0 ) {
            throw new RuntimeException("연관 게시글이 존재하여 삭제할 수 없습니다." +
                    "해당 카테고리의 글을 모두 지워주세요.");
        }
        
        //얕은 삭제 해주기
        category.setDeleted(true);
    }
}
