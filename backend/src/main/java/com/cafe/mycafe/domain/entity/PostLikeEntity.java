package com.cafe.mycafe.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "POST_LIKES")
public class PostLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_like_seq")
    @SequenceGenerator(name = "post_like_seq", sequenceName = "POST_LIKE_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;

    // 좋아요를 누른 시간 (Builder 패턴 사용 시에도 기본값 자동 설정)
    @Column(nullable = false, name = "LIKED_AT")
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist(){
        if (this.likedAt == null) {
            this.likedAt = LocalDateTime.now();
        }
    }
}
