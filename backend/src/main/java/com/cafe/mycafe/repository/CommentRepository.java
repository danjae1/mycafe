package com.cafe.mycafe.repository;


import com.cafe.mycafe.domain.entity.CommentEntity;
import com.cafe.mycafe.domain.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPost(PostEntity postEntity);  // 게시글 댓글 조회
    List<CommentEntity> findByParent(CommentEntity parent); // 대댓글 조회

    @Query("SELECT c FROM Comment c JOIN c.user u WHERE u.id = :userId")
    List<CommentEntity> findAllByUserId(@Param("userId") Long userId);
}
