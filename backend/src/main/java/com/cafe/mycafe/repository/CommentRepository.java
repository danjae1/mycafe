package com.cafe.mycafe.repository;


import com.cafe.mycafe.domain.entity.CommentEntity;
import com.cafe.mycafe.domain.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPost(PostEntity post);  // 게시글 댓글 조회
    List<CommentEntity> findByParent(CommentEntity parent); // 대댓글 조회
    
    //게시글 Id로 삭제되지 않은 댓글 전체 조회하기
    List<CommentEntity> findAllByPostIdAndDeletedFalse(Long postId);

    // 특정 유저가 작성한 삭제되지 않은 댓글 조회
    //Page<CommentEntity> findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    //해당 유저가 쓴 댓글 조회하기
    List<CommentEntity> findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc(@Param("userId") Long userId);
}
