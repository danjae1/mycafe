package com.cafe.mycafe.domain.dto.CommentDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    @NotBlank(message = "내용을 입력해주세요!")
    private String content;

    //대댓글일 경우 부모 댓글의 ID
    private Long parentId;
}
