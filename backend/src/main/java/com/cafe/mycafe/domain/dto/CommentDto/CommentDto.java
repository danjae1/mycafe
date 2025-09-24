package com.cafe.mycafe.domain.dto.CommentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private int num;
    private String writer;
    private String targetName;
    private String content;
    private int groupNum;
    private int parentNum;
    private boolean deleted;
    private String createdAt;
    private String profileImage;
    private int replyCount;
}
