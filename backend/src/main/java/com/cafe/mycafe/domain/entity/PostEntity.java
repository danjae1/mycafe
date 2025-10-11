package com.cafe.mycafe.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String writer;
    private String title;
    private String imageUrl;
    private String thumbnailUrl;

    @Lob
    @Column(nullable = false)
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;
    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    @ManyToOne(fetch =FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0 ){
            this.likeCount -= 1;
        }
    }

}
