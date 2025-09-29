package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.dto.PostDto.PostResponseDto;
import com.cafe.mycafe.domain.entity.CategoryEntity;
import com.cafe.mycafe.domain.entity.PostEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    // 카테고리별 게시글 조회
    List<PostEntity> findAllByCategoryAndDeletedFalseOrderByCreatedAtDesc(CategoryEntity category, Pageable pageable);

    //전체 글 목록 조회
    List<PostEntity> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    //유저가 쓴 글 목록 조회
    List<PostEntity> findAllByAuthorIdOrderByCreatedAtDesc(Long userId);
    
    // 삭제 처리 안 된 모든 글 수
    int countAllByDeletedFalse();

    // 카테고리별 글 수
    int countByCategory(CategoryEntity category);

}
