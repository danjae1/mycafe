package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.CategoryEntity;
import com.cafe.mycafe.domain.entity.PostEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    int countByUserId(Long userId);

    // 해당 카테고리 존재하는지 여부판단하기
    int countByCategoryAndDeletedFalse(CategoryEntity category);

    // 전체 게시글 페이징 (NativeQuery + ROW_NUMBER)
    @Query(value = """
        SELECT * FROM (
            SELECT p.*, ROW_NUMBER() OVER (ORDER BY created_at DESC) AS rn
            FROM post_entity p
            WHERE deleted = 0
        )
        WHERE rn BETWEEN :startRow AND :endRow
        """, nativeQuery = true)
    List<PostEntity> findPostsWithPaging(
            @Param("startRow") int startRow,
            @Param("endRow") int endRow
    );

    // 카테고리별 게시글 페이징
    @Query(value = """
        SELECT * FROM (
            SELECT p.*, ROW_NUMBER() OVER (ORDER BY created_at DESC) AS rn
            FROM post_entity p
            WHERE deleted = 0 AND category_id = :categoryId
        )
        WHERE rn BETWEEN :startRow AND :endRow
        """, nativeQuery = true)
    List<PostEntity> findByCategoryWithPaging(
            @Param("categoryId") Long categoryId,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow
    );

    // 전체 게시글 수
    @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.deleted = false")
    int countAllByDeletedFalse();

    // 카테고리별 게시글 수 (NativeQuery)
    @Query(value = "SELECT COUNT(*) FROM post_entity WHERE deleted = 0 AND category_id = :categoryId", nativeQuery = true)
    int countByCategoryNative(@Param("categoryId") Long categoryId);

    // 유저가 쓴 글 목록 조회
    List<PostEntity> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

}
