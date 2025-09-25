package com.cafe.mycafe.domain.dto.PostDto;


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
    private Long id;
    private String title;
    private String content;
    private String author;      // 서버에서 넣어주는 작성자
    private String imageUrl;    // 업로드된 이미지 접근 URL
    private LocalDateTime createdAt;

}