package com.cafe.mycafe.domain.dto.PostDto;

//좋아요 누르면 클라이언트 화면에 바로 반환하기 위해 필요

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeResponseDto {
    private boolean liked;   // 사용자가 지금 눌렀는지
    private int likeCount;   // 현재 총 좋아요 수
}
