package com.cafe.mycafe.controller.post;


import com.cafe.mycafe.controller.exceptioncontroller.CategoryNotFoundException;
import com.cafe.mycafe.domain.dto.CommentDto.CommentListItemDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListItemDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListResponse;
import com.cafe.mycafe.domain.dto.PostDto.PostRequestDto;
import com.cafe.mycafe.domain.dto.PostDto.PostResponseDto;
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

    // 타 유저
    @GetMapping("/users/{userId}/replied")
    public ResponseEntity<List<PostListItemDto>> getUserComments(@PathVariable Long userId){
        List<PostListItemDto> comments = postService.getPostsCommentedByUser(userId, null);
        return ResponseEntity.ok(comments);
    }

    // 본인
    @GetMapping("/comments/replied/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PostListItemDto>> getMyComments(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(postService.getPostsCommentedByUser(userDetails.getId(), userDetails.getId()));
    }

    //마이페이지 내가 쓴 글 목록 불러오기
    @GetMapping("/posts/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PostListItemDto>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long currentUserId = userDetails.getId(); // 로그인한 사용자 ID

        //마이페이지니까 인자 둘다 나의 Id 삽입
        List<PostListItemDto> posts = postService.getPostSummariesByUserId(currentUserId, currentUserId);

        return ResponseEntity.ok(posts);
    }

    //타유저 프로필에서 해당 유저가 쓴 글 목록 불러오기
    @GetMapping("/users/{targetUserId}/posts")
    public ResponseEntity<List<PostListItemDto>> getUserPosts(@PathVariable Long targetUserId,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails){
        //targetUserId => 내가 보려고 하는 유저의 ID
        //로그인 했는지 검증
        Long currentUserId = (userDetails != null) ? userDetails.getId() : null;

        List<PostListItemDto> posts = postService.getPostSummariesByUserId(targetUserId, currentUserId);

        return ResponseEntity.ok(posts);
    }

    //전체 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(required = false) String categoryPath,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long currentUserId
    ) {
        try {
            PostListResponse response = postService.getPostsByPath(categoryPath, keyword, pageNum, pageSize, currentUserId);
            return ResponseEntity.ok(response);
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류 발생"));
        }
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
