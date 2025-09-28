package com.cafe.mycafe.domain.dto.PostDto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Lob;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    //단일 게시글 반환
    private Long id; //게시글 고유번호
    private String writer;      // 서버에서 넣어주는 작성자
    private String title; //제목
    private String content; //본문
    private Long categoryId;
    private String categoryName;
    private Long viewCount; //조회수
    private Long likeCount; //좋아요 수
    private boolean likedByUser;
    private String imageUrl;    // 업로드된 이미지 접근 URL
    private String thumbnailUrl;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH:mm")
    private LocalDateTime createdAt;




}