package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.Comment;
import com.cafe.mycafe.domain.entity.CommentLike;
import com.cafe.mycafe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentAndMember(Comment comment, User user); // 좋아요 중복 방지
}
