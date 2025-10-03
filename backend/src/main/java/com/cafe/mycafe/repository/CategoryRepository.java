package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    //카테고리 목록 전체 조회
    Optional<CategoryEntity> findByPathAndDeletedFalse(String name); // 이름으로 카테고리 조회

    List<CategoryEntity> findAllByDeletedFalseOrderByCreatedAtDesc();
    //카테고리 생성시 같은 이름 (중복 방지용)
    boolean existsByNameAndDeletedFalse(String name);
}
