package com.cafe.mycafe.controller.post;

import com.cafe.mycafe.domain.dto.PostDto.PostLikeResponseDto;
import com.cafe.mycafe.security.CustomUserDetails;
import com.cafe.mycafe.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    //마이페지이 내가 좋아요한 글 목록 불러오기
    @GetMapping("/liked")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> getLikedPostByMe(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        List<Long> likedPostIds = postLikeService.getLikedPostIdsByUser(userId);
        return ResponseEntity.ok(likedPostIds);
    }

    //타 유저 좋아요한 글 목록 불러오기
    @GetMapping("/{userId}/liked")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> getLikedPostByUser(@PathVariable  Long userId){

        List<Long> likedPostIds = postLikeService.getLikedPostIdsByUser(userId);
        return ResponseEntity.ok(likedPostIds);
    }

    //단일 게시글 좋아요 여부 확인 Ui용
    @GetMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isLikeByUser(@PathVariable Long postId,
                                                Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        boolean liked = postLikeService.isLikeByUser(postId, userId);
        return ResponseEntity.ok(liked);
    }

    //게시글에 좋아요 누르기
    @PostMapping("/post/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostLikeResponseDto> toggleLike(@PathVariable Long postId,
                                                          @AuthenticationPrincipal Long userId){

        PostLikeResponseDto response = postLikeService.toggleLike(postId, userId);

        return ResponseEntity.ok(response);
    }
}
