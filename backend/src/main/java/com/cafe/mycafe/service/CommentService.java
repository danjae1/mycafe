package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.CommentDto.CommentListItemDto;
import com.cafe.mycafe.domain.dto.CommentDto.CommentRequestDto;
import com.cafe.mycafe.domain.dto.CommentDto.CommentResponseDto;
import com.cafe.mycafe.domain.dto.common.PageResult;

import java.util.List;

public interface CommentService {

    //댓글 작성하기 (부모 댓글이 있다면 대댓글로 들어감)
    CommentResponseDto createComment(Long postId, Long userId, CommentRequestDto dto);

    //특정 게시물의 댓글 전체 조회
    List<CommentResponseDto> getCommentsByPost(Long postId, Long userId);

    //댓글 수정하기
    CommentResponseDto updateComment(Long commentId, Long userId, CommentRequestDto dto);

    //댓글삭제하기
    void deleteComment(Long commentId, Long userId);

    PageResult<CommentListItemDto> getCommentSummariesByUserId(Long targetUserId, Long userId, int pageNum, int pageSize);

}
