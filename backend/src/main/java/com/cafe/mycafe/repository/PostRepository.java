package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.Category;
import com.cafe.mycafe.domain.entity.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 카테고리별 게시글 조회
    List<Post> findByCategory(Category category);

    // 제목/내용 검색
    List<Post> findByTitleContainingOrContentContaining(String title, String content);

    // 좋아요 수 기준 정렬
    List<Post> findAllByOrderByLikeCountDesc();
}
