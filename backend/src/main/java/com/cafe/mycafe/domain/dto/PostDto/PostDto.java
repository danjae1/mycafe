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
public class PostDto {

    private int num;
    private String writer;
    private String title;
    private String content;
    private int viewCount;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH:mm")
    private LocalDateTime createdAt;

    private String grade;         // 작성자 회원 등급
    private String profileImage;
    private int startRowNum;
    private int endRowNum;
    private int prevNum;
    private int nextNum;

    private String keyword;
    private String search;

    private int likeCount; // 총 추천 수
    private boolean likedByUser; // 로그인 유저가 이미 추천 했는지 여부 (Ui에 색상 다르게 적용하기 위해서)
}
