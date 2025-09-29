package com.cafe.mycafe.domain.dto.CommentDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentLikeResponseDto {
    private Long commentId;
    //해당 댓글의 총 좋아요 수
    private int likeCount;
    // 내가 좋아요 눌렀는지 여부
    private boolean likedByMe;
}
