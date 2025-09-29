package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {

    // 좋아요 중복 방지
    boolean existsByCommentAndUser(CommentEntity commentEntity, UserEntity user);

    // 댓글별 좋아요 개수 조회
    @Query("SELECT c.comment.id, COUNT(c) FROM CommentLikeEntity c WHERE c.comment.id IN :commentIds GROUP BY c.comment.id")
    List<Object[]> countLikesByCommentIds(@Param("commentIds") List<Long> commentIds);

    // 댓글 목록 조회시 내가 좋아요 눌렀는지 여부 조회
    @Query("SELECT c.comment.id FROM CommentLikeEntity c WHERE c.comment.id IN :commentIds AND c.user.id = :userId")
    List<Long> findLikedCommentIdsByUser(@Param("commentIds") List<Long> commentIds, @Param("userId") Long userId);

    // 단일 댓글 + 단일 유저 좋아요 조회
    // 좋아요가 이미 존재하는지 확인 좋아요 취소할 떄 entity 필요함
    Optional<CommentLikeEntity> findByCommentAndUser(CommentEntity comment, UserEntity user);

    int countByComment(CommentEntity comment);

    @Query("SELECT cl FROM CommentLikeEntity cl JOIN cl.comment c WHERE cl.user.id = :userId")
    List<CommentLikeEntity> findAllByUserId(@Param("userId") Long userId);

}
