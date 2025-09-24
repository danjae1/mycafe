package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.CommentEntity;
import com.cafe.mycafe.domain.entity.CommentLikeEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    boolean existsByCommentAndUser(CommentEntity commentEntity, UserEntity user); // 좋아요 중복 방지
}
