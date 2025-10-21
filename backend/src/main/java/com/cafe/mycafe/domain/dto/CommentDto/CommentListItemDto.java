package com.cafe.mycafe.domain.dto.CommentDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentListItemDto {
    private Long id;
    private String writer;        // 댓글 작성자
    private String content;       // 댓글 내용
    private String postTitle;     // 댓글 단 게시글 제목
    private Long postId;
    private LocalDateTime createdAt;
    private int likeCount;        // 댓글 좋아요 개수
    private boolean likedByUser;  // 현재 로그인 사용자가 좋아요 했는지
}