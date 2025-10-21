package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.PostLikeEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

    // 단순 존재 여부 확인 (boolean)
    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN true ELSE false END " +
            "FROM PostLikeEntity pl WHERE pl.post = :post AND pl.user = :user")
    boolean existsByPostAndUser(@Param("post") PostEntity post, @Param("user") UserEntity user);

    // 삭제/수정용 엔티티 조회
    Optional<PostLikeEntity> findByPostAndUser(PostEntity post, UserEntity user);

//    // 유저가 좋아요 누른 글 전체 조회
//    @Query("SELECT pl FROM PostLikeEntity pl JOIN pl.post p WHERE p.user.id = :userId")
//    List<PostLikeEntity> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT pl FROM PostLikeEntity pl WHERE pl.user.id = :userId")
    List<PostLikeEntity> findAllByUserId(@Param("userId") Long userId);

    // 유저가 좋아요를 누른 게시글 목록을 페이징 처리하여 조회 (네이티브 쿼리)
    // POST_LIKES 테이블과 POST_ENTITY 테이블을 조인하여 좋아요 누른 시간 기준 내림차순 정렬
    @Query(value = """
    SELECT sub.ID, sub.WRITER, sub.TITLE, sub.IMAGE_URL, sub.THUMBNAIL_URL, sub.CONTENT,
           sub.CREATED_AT, sub.UPDATED_AT, sub.DELETED, sub.VIEW_COUNT, sub.LIKE_COUNT,
           sub.USER_ID, sub.CATEGORY_ID
    FROM (
        SELECT p.ID, p.WRITER, p.TITLE, p.IMAGE_URL, p.THUMBNAIL_URL, p.CONTENT,
               p.CREATED_AT, p.UPDATED_AT, p.DELETED, p.VIEW_COUNT, p.LIKE_COUNT,
               p.USER_ID, p.CATEGORY_ID,
               ROW_NUMBER() OVER (ORDER BY pl.LIKED_AT DESC) AS rn
        FROM POST_ENTITY p
        INNER JOIN POST_LIKES pl ON pl.POST_ID = p.ID
        WHERE pl.USER_ID = :userId
    ) sub
    WHERE sub.rn BETWEEN :startRow AND :endRow
    """, nativeQuery = true)
        List<PostEntity> findLikedPostsByUserWithPaging(
                @Param("userId") Long userId,
                @Param("startRow") int startRow,
                @Param("endRow") int endRow
        );

    // 유저가 좋아요를 누른 총 게시글 개수 조회 (네이티브 쿼리)
    @Query(value = """
    SELECT COUNT(*)
    FROM POST_LIKES
    WHERE USER_ID = :userId
    """, nativeQuery = true)
    int countLikedPostsByUser(@Param("userId") Long userId);

    // 특정 게시글 좋아요 수
    Long countByPostId(Long postId);

    // 관계 기반 존재 여부 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    int countByUserId(Long userId);
}
