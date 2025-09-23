package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.CategoryEntity;
import com.cafe.mycafe.domain.entity.PostEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    // 카테고리별 게시글 조회
    List<PostEntity> findByCategory(CategoryEntity categoryEntity);

    // 제목/내용 검색
    List<PostEntity> findByTitleContainingOrContentContaining(String title, String content);

    // 좋아요 수 기준 정렬
    List<PostEntity> findAllByOrderByLikeCountDesc();
}
