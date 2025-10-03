package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.PostViewEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;

public interface PostViewRepository extends JpaRepository<PostViewEntity,Long> {


    //최근 1시간 내에 조회 기록이 있는지 조회 중복 체크
    @Query("SELECT CASE WHEN COUNT(pv) > 0 THEN true ELSE false END " +
            "FROM PostViewEntity pv " +
            "WHERE pv.post.id = :postId " +
            "AND pv.user.id = :userId " +
            "AND pv.viewedAt > :viewedAt")
    boolean existsRecentView(@Param("postId") Long postId,
                             @Param("userId") Long userId,
                             @Param("viewedAt") LocalDateTime viewedAt);
}
