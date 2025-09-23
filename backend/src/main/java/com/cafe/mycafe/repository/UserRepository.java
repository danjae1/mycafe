package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username); // 로그인/회원 조회
}
