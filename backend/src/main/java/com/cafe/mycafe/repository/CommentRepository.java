package com.cafe.mycafe.repository;


import com.cafe.mycafe.domain.entity.Comment;
import com.cafe.mycafe.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);  // 게시글 댓글 조회
    List<Comment> findByParent(Comment parent); // 대댓글 조회
}
