package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.Post;
import com.cafe.mycafe.domain.entity.PostLike;
import com.cafe.mycafe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostAndMember(Post post, User user); // 좋아요 중복 방지
}
