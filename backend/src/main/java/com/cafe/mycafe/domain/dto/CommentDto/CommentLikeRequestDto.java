package com.cafe.mycafe.domain.dto.CommentDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeRequestDto {

    private Long commentId; //좋아요 누를 댓글의 Id
}
