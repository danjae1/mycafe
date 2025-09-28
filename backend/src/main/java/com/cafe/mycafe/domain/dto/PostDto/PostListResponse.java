package com.cafe.mycafe.domain.dto.PostDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class PostListResponse {
    // 게시글 목록 응답
    private List<PostListItemDto> list; // 게시글 목록
    private PageResponse page;          // 페이징 + 검색 정보
}
