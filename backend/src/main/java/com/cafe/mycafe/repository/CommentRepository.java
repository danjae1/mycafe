package com.cafe.mycafe.repository;


import com.cafe.mycafe.domain.entity.CommentEntity;
import com.cafe.mycafe.domain.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    //userId의 댓글 수
    int countByUserId(Long userId);

    List<CommentEntity> findByPost(PostEntity post);  // 게시글 댓글 조회
    List<CommentEntity> findByParent(CommentEntity parent); // 대댓글 조회
    
    //게시글 Id로 삭제되지 않은 댓글 전체 조회하기
    List<CommentEntity> findAllByPostId(Long postId);

    // 특정 유저가 작성한 삭제되지 않은 댓글 조회
    //Page<CommentEntity> findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    //해당 유저가 쓴 댓글 조회하기
    List<CommentEntity> findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 유저가 작성한 삭제되지 않은 댓글의 총 개수 조회 (네이티브 쿼리)
    @Query(
            value = "SELECT COUNT(*) FROM COMMENTS c WHERE c.USER_ID = :userId AND c.DELETED = 0",
            nativeQuery = true
    )
    Long countCommentsByUser(@Param("userId") Long userId);


    // 유저가 작성한 댓글 목록을 페이징 처리하여 조회 (네이티브 쿼리)
    @Query(
            value = "SELECT c.ID, c.CONTENT, c.POST_ID, c.USER_ID, c.LIKE_COUNT, " +
                    "c.PARENT_ID, c.CREATED_AT, c.UPDATED_AT, c.DELETED " +
                    "FROM (" +
                    " SELECT c.*, ROW_NUMBER() OVER (ORDER BY c.CREATED_AT DESC) rn " +
                    " FROM COMMENTS c " +
                    " WHERE c.USER_ID = :userId AND c.DELETED = 0" +
                    ") c WHERE rn BETWEEN :start AND :end",
            nativeQuery = true
    )
    List<CommentEntity> findPostsCommentsByUserWithPaging(
            @Param("userId") Long userId,
            @Param("start") int start,
            @Param("end") int end
    );
}
