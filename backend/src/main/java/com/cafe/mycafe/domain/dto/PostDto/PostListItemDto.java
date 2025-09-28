package com.cafe.mycafe.domain.dto.PostDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostListItemDto {
    //게시글 목록에서 게시글 한 줄에 대한 정보들

    private Long id;
    private String writer;
    private String title;
    private int likeCount;
    private boolean likedByUser;
    private String thumbnailUrl;
    private String categoryName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH:mm")
    private LocalDateTime createdAt;
}
