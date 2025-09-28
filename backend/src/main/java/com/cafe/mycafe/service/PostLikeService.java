package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.PostDto.PostLikeResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostLikeService {
    
    // 게시글 좋아요 추가/취소 토글 로직
    PostLikeResponseDto toggleLike(Long postId, Long userId);
    
    //특정 게시글에 대해 사용자가 이미 좋아요 눌렀는지 확인 조회용

    boolean isLikeByUser(Long postId, Long userId);
    
    //특정 사용자가 좋아요 누른 게시글 목록 조회
    //마이페이지에서 내가 좋아요 누른 글  불러올 때 사용
    List<Long> getLikedPostIdsByUser(Long userId); 
}
