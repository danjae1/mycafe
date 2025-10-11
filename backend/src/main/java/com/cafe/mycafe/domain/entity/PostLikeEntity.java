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
@Table(name = "post_likes")
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

    @Column(nullable = false)
    private LocalDateTime likedAt;

    @PrePersist
    public void prePersist(){
        if (this.likedAt == null) {
            this.likedAt = LocalDateTime.now();
        }
    }
}
