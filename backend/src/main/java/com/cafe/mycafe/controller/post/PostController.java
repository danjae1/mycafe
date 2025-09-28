package com.cafe.mycafe.controller.post;

import com.cafe.mycafe.domain.dto.PostDto.PostLikeResponseDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListResponse;
import com.cafe.mycafe.domain.dto.PostDto.PostRequestDto;
import com.cafe.mycafe.domain.dto.PostDto.PostResponseDto;
import com.cafe.mycafe.security.CustomUserDetails;
import com.cafe.mycafe.service.PostLikeService;
import com.cafe.mycafe.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    //마이페지이 내가 좋아요한 글 확인하기
    @GetMapping("/liked")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> getLikedPostByUser(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        List<Long> likedPostIds = postLikeService.getLikedPostIdsByUser(userId);
        return ResponseEntity.ok(likedPostIds);

    }

    //단일 게시글 좋아요 여부 확인
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
    
    //전체 게시글 조회
    @GetMapping
    public PostListResponse getPosts(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long currentUserId
    ) {
        return postService.getPosts(categoryName, keyword, pageNum, pageSize, currentUserId);
    }

    //게시글 등록
    @PostMapping("/post")
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
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deletePost(@PathVariable Long id,
                                             @AuthenticationPrincipal CustomUserDetails user) {

        postService.deletePost(id, user.getId());
        return ResponseEntity.ok("삭제되었습니다.");
    }

    //게시글 수정
    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody PostRequestDto dto) {
        PostResponseDto response = postService.updatePost(id, user.getId(), dto);
        return ResponseEntity.ok(response);
    }
}
