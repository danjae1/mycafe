package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.PostLikeEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {
    boolean existsByPostAndUser(PostEntity postEntity, UserEntity user); // 좋아요 중복 방지
    @Query("SELECT pl FROM PostLikeEntity pl JOIN pl.post p WHERE p.user.id = :userId")
    List<PostLikeEntity> findAllByUserId(@Param("userId") Long userId);
}
