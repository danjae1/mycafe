package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.dto.PostDto.PostListItemDto;
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
        SELECT p.ID, p.WRITER, p.TITLE, p.IMAGE_URL, p.THUMBNAIL_URL, p.CONTENT,
               p.CREATED_AT, p.UPDATED_AT, p.DELETED, p.VIEW_COUNT, p.LIKE_COUNT,
               p.USER_ID, p.CATEGORY_ID
        FROM (
            SELECT p.*, ROW_NUMBER() OVER (ORDER BY p.CREATED_AT DESC) AS rn
            FROM POST_ENTITY p
            WHERE p.DELETED = 0
        ) p
        WHERE rn BETWEEN :startRow AND :endRow
        """, nativeQuery = true)
    List<PostEntity> findPostsWithPaging(
            @Param("startRow") int startRow,
            @Param("endRow") int endRow
    );

    // 카테고리별 게시글 페이징
    @Query(value = """
        SELECT p.ID, p.WRITER, p.TITLE, p.IMAGE_URL, p.THUMBNAIL_URL, p.CONTENT,
               p.CREATED_AT, p.UPDATED_AT, p.DELETED, p.VIEW_COUNT, p.LIKE_COUNT,
               p.USER_ID, p.CATEGORY_ID
        FROM (
            SELECT p.*, ROW_NUMBER() OVER (ORDER BY p.CREATED_AT DESC) AS rn
            FROM POST_ENTITY p
            WHERE p.DELETED = 0 AND p.CATEGORY_ID = :categoryId
        ) p
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
    @Query(value = "SELECT COUNT(*) FROM POST_ENTITY WHERE DELETED = 0 AND CATEGORY_ID = :categoryId", nativeQuery = true)
    int countByCategoryNative(@Param("categoryId") Long categoryId);

    // 유저가 쓴 글 목록 조회
    List<PostEntity> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    //totalrow 가져오기
    @Query(value = """
    SELECT COUNT(DISTINCT p.ID)
    FROM POST_ENTITY p
    JOIN COMMENTS c ON c.POST_ID = p.ID
    WHERE c.USER_ID = :userId AND c.DELETED = 0
""", nativeQuery = true)
    int countPostsCommentedByUser(@Param("userId") Long userId);

    // 유저가 댓글 남긴 글의 목록 가져오기
    @Query(value = """
    SELECT p.ID, p.WRITER, p.TITLE, p.IMAGE_URL, p.THUMBNAIL_URL, p.CONTENT,
           p.CREATED_AT, p.UPDATED_AT, p.DELETED, p.VIEW_COUNT, p.LIKE_COUNT,
           p.USER_ID, p.CATEGORY_ID
    FROM (
        SELECT DISTINCT p.*, ROW_NUMBER() OVER (ORDER BY p.CREATED_AT DESC) AS rn
        FROM POST_ENTITY p
        WHERE EXISTS (
            SELECT 1 FROM COMMENTS c
            WHERE c.POST_ID = p.ID AND c.USER_ID = :userId AND c.DELETED = 0
        ) AND p.DELETED = 0
    ) p
    WHERE rn BETWEEN :startRow AND :endRow
    """, nativeQuery = true)
    List<PostEntity> findPostsCommentedByUserWithPaging(
            @Param("userId") Long userId,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow
    );

    @Query(value = """
    SELECT p.ID, p.WRITER, p.TITLE, p.IMAGE_URL, p.THUMBNAIL_URL, p.CONTENT,
           p.CREATED_AT, p.UPDATED_AT, p.DELETED, p.VIEW_COUNT, p.LIKE_COUNT,
           p.USER_ID, p.CATEGORY_ID
    FROM (
        SELECT p.*, ROW_NUMBER() OVER(ORDER BY p.CREATED_AT DESC) rn
        FROM POST_ENTITY p
        WHERE p.USER_ID = :userId AND p.DELETED = 0
    ) p
    WHERE rn BETWEEN :startRow AND :endRow
    """, nativeQuery = true)
    List<PostEntity> findPostsByUserWithPaging(@Param("userId") Long userId,
                                               @Param("startRow") int startRow,
                                               @Param("endRow") int endRow);



    @Query(value = "SELECT COUNT(p.ID) FROM POST_ENTITY p WHERE p.USER_ID = :userId AND p.DELETED = 0", nativeQuery = true)
    int countPostsByUser(@Param("userId") Long userId);

}
