package com.cafe.mycafe.controller.post;


import com.cafe.mycafe.controller.exceptioncontroller.CategoryNotFoundException;
import com.cafe.mycafe.domain.dto.CommentDto.CommentListItemDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListItemDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListResponse;
import com.cafe.mycafe.domain.dto.PostDto.PostRequestDto;
import com.cafe.mycafe.domain.dto.PostDto.PostResponseDto;
import com.cafe.mycafe.domain.dto.common.PageResult;
import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.security.CustomUserDetails;
import com.cafe.mycafe.service.CommentService;
import com.cafe.mycafe.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    // 타 유저가 댓글 단 글의 목록
    @GetMapping("/users/{userId}/replied")
    public ResponseEntity<PageResult<PostListItemDto>> getUserCommentedPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResult<PostListItemDto> result = postService.getPostsCommentedByUser(userId, null, pageNum, pageSize);
        return ResponseEntity.ok(result);
    }



    // 내가 댓글 단 글의 목록
    @GetMapping("/comments/replied/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResult<PostListItemDto>> getMyCommentedPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResult<PostListItemDto> result = postService.getPostsCommentedByUser(userDetails.getId(), userDetails.getId(), pageNum, pageSize);
        return ResponseEntity.ok(result);
    }


    //마이페이지 내가 쓴 글 목록 불러오기
    @GetMapping("/posts/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResult<PostListItemDto>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        PageResult<PostListItemDto> posts = postService.getPostSummariesByUserId(userDetails.getId(), userDetails.getId(), pageNum, pageSize);
        return ResponseEntity.ok(posts);
    }


    //타유저 프로필에서 해당 유저가 쓴 글 목록 불러오기
    @GetMapping("/users/{targetUserId}/posts")
    public ResponseEntity<PageResult<PostListItemDto>> getUserPosts(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Long currentUserId = (userDetails != null) ? userDetails.getId() : null;
        PageResult<PostListItemDto> posts = postService.getPostSummariesByUserId(targetUserId, currentUserId, pageNum, pageSize);
        return ResponseEntity.ok(posts);
    }


    //전체 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<PageResult<PostListItemDto>> getPosts(
            @RequestParam(required = false) String categoryPath,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long currentUserId
    ) {

            PageResult<PostListItemDto> response = postService.getPostsByPath(categoryPath, keyword, pageNum, pageSize, currentUserId);
            return ResponseEntity.ok(response);
    }


        //단일 게시글 조회
        @GetMapping("/{categoryPath}/posts/{postId}")
        ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId,
                                                    @PathVariable String categoryPath,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
            Long userId = (userDetails != null) ? userDetails.getId() : null;

            PostResponseDto response = postService.getPostById(postId, userId);

            return ResponseEntity.ok(response);
        }

    //게시글 등록
    @PostMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto> createPost(@AuthenticationPrincipal CustomUserDetails user,
                                                      @RequestBody PostRequestDto dto
//                                                    ,@RequestPart(value = "image",required = false)
//                                                      MultipartFile image
    ) {

        PostResponseDto response = postService.createPost(user.getId(), dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //게시글 삭제
    @DeleteMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deletePost(@PathVariable Long postId,
                                             @AuthenticationPrincipal CustomUserDetails user) {

        postService.deletePost(postId, user.getId());
        return ResponseEntity.ok("삭제되었습니다.");
    }

    //게시글 수정
    @PatchMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody PostRequestDto dto) {
        ;
        PostResponseDto response = postService.updatePost(postId, user.getId(), dto);
        return ResponseEntity.ok(response);
    }
}
