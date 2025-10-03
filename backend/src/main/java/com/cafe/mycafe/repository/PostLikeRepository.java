package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.PostLikeEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

    // 단순 존재 여부 확인 (boolean)
    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN true ELSE false END " +
            "FROM PostLikeEntity pl WHERE pl.post = :post AND pl.user = :user")
    boolean existsByPostAndUser(@Param("post") PostEntity post, @Param("user") UserEntity user);

    // 삭제/수정용 엔티티 조회
    Optional<PostLikeEntity> findByPostAndUser(PostEntity post, UserEntity user);

    // 유저가 좋아요 누른 글 전체 조회
    @Query("SELECT pl FROM PostLikeEntity pl JOIN pl.post p WHERE p.user.id = :userId")
    List<PostLikeEntity> findAllByUserId(@Param("userId") Long userId);

    // 특정 게시글 좋아요 수
    Long countByPostId(Long postId);
}
