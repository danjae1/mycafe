package com.cafe.mycafe.domain.dto.CommentDto;

import com.cafe.mycafe.domain.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    
    private Long id;
    private String content;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    
    private int likeCount; //댓글 좋아요 수
    private boolean likedByMe; //내가 좋아요 눌렀는지 확인 UI용
    
    private List<CommentResponseDto> children; //대댓글 리스트

}
