package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.CommentDto.CommentLikeResponseDto;

import java.util.List;
import java.util.Map;

public interface CommentLikeService {
    
    //댓글 좋아요 추가 + 취소
    CommentLikeResponseDto toggleLike(Long commentId, Long userId);

    // 특정 댓글에 대해 사용자가 이미 좋아요 눌렀는지 확인
    boolean isLikeByUser(Long commentId, Long userId);

    // 특정 사용자가 좋아요 누른 댓글 ID 목록 조회 (댓글 목록 표시용)
    List<Long> getLikedCommentIdsByUser(Long userId);

    // 여러 댓글에 대해 좋아요 개수 한 번에 조회 (댓글 목록 표시용)
    //게시글 들어갔을 때 댓글목록 불러옴 -> CommentResponseDto에 좋아요갯수와 내가 눌렀는지 확인하는 필드 있음
    // 하지만 댓글이 100개라면 하나하나 조회하면서 좋아요 갯수를 가져와야하기 떄문에 100번 조회해야함
    // getLiekCountForComments로 댓글들의 좋아요 갯수를 한 번에 조회해서 db접근을 1번만 하게 해줌
    Map<Long, Integer> getLikeCountForComments(List<Long> commentIds);
}
