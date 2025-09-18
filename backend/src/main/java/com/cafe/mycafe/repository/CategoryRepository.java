package com.cafe.mycafe.repository;

import com.cafe.mycafe.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name); // 이름으로 카테고리 조회
}
