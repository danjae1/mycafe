package com.cafe.mycafe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_seq", allocationSize = 1)
    private Long id;

    private String description;

    private LocalDateTime createdAt;
    
    //컴포넌트 동적 라우팅하기 위해 path필요
    @Column(name = "category_path",nullable = false, unique = true)
    private String path; // 예: "/free", "/question"
    
    @Column(nullable = false, unique = true)
    private String name; // 예: "자유게시판", "질문게시판"

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

}
