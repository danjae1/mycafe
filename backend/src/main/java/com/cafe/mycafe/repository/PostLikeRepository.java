package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.Post;
import com.cafe.mycafe.domain.entity.PostLike;
import com.cafe.mycafe.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostAndMember(Post post, User user); // 좋아요 중복 방지
    @Query("SELECT pl FROM PostLike pl JOIN pl.post p WHERE p.user.id = :userId")
    List<PostLike> findAllByUserId(@Param("userId") Long userId);
}
