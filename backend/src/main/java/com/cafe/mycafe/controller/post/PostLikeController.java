package com.cafe.mycafe.controller.post;

import com.cafe.mycafe.domain.dto.PostDto.PostLikeResponseDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListItemDto;
import com.cafe.mycafe.domain.dto.common.PageResult;
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

    // 마이페이지 내가 좋아요한 글 목록
    @GetMapping("/posts/liked/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResult<PostListItemDto>> getLikedPostByMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Long userId = userDetails.getId();
        PageResult<PostListItemDto> likedPosts = postLikeService.getLikedPostsByUser(userId, pageNum, pageSize);
        return ResponseEntity.ok(likedPosts);
    }

    // 타 유저 좋아요한 글 목록
    @GetMapping("/users/{userId}/posts/liked")
    public ResponseEntity<PageResult<PostListItemDto>> getLikedPostByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResult<PostListItemDto> likedPosts = postLikeService.getLikedPostsByUser(userId, pageNum, pageSize);
        return ResponseEntity.ok(likedPosts);
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
    @PostMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostLikeResponseDto> toggleLike(@PathVariable Long postId,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails){

        Long userId = userDetails.getId();
        PostLikeResponseDto response = postLikeService.toggleLike(postId, userId);

        return ResponseEntity.ok(response);
    }
}
