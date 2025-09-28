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
    
    //중복 방지용 -> 이 유저가 좋아요 눌렀는지 안 눌렀는지 확인"만"
    // 하지만 좋아요 취소해야할 때 postLikeEntity가 필요해서
    // findbypostanduser를 또 호출해야하기 때문에 삭제할 엔티티도 얻을 수 있게 만든다.
    //게시글 조회할 때 조회용
    boolean existsByPostAndUser(PostEntity post, UserEntity user); 


    //좋아요가 이미 존재하는지 확인할 때 사용
    // 좋아요 취소할 때 엔티티가 필요하기 떄문에 필요함
    Optional<PostLikeEntity> findByPostAndUser(PostEntity post, UserEntity user);

    @Query("SELECT pl FROM PostLikeEntity pl JOIN pl.post p WHERE p.user.id = :userId")
    List<PostLikeEntity> findAllByUserId(@Param("userId") Long userId);
}
