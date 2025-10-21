package com.cafe.mycafe.controller.comment;

import com.cafe.mycafe.domain.dto.CommentDto.CommentListItemDto;
import com.cafe.mycafe.domain.dto.CommentDto.CommentRequestDto;
import com.cafe.mycafe.domain.dto.CommentDto.CommentResponseDto;
import com.cafe.mycafe.domain.dto.common.PageResult;
import com.cafe.mycafe.domain.entity.CommentEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.CommentLikeRepository;
import com.cafe.mycafe.repository.CommentRepository;
import com.cafe.mycafe.repository.UserRepository;
import com.cafe.mycafe.security.CustomUserDetails;
import com.cafe.mycafe.service.CommentLikeService;
import com.cafe.mycafe.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {

    private final CommentService commentService;

    // 마이페이지에서 현재 로그인한 유저가 작성한 댓글 목록 조회
    @GetMapping("/comments/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResult<CommentListItemDto>> getMyComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Long userId = userDetails.getId();
        PageResult<CommentListItemDto> comments = commentService.getCommentSummariesByUserId(userId, userId, pageNum, pageSize);

        return ResponseEntity.ok(comments);
    }

    // 특정 유저가 작성한 댓글 목록 조회 (다른 유저 프로필 페이지용)
    @GetMapping("/comments/{targetUserId}")
    public ResponseEntity<PageResult<CommentListItemDto>> getCommentsByUser(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Long userId = (userDetails != null) ? userDetails.getId() : null;

        PageResult<CommentListItemDto> comments = commentService.getCommentSummariesByUserId(targetUserId, userId, pageNum, pageSize);

        return ResponseEntity.ok(comments);
    }



    //게시글 조회시 댓글 목록 같이 불러오기
    @GetMapping("/{categoryPath}/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        List<CommentResponseDto> comments = commentService.getCommentsByPost(postId, userDetails.getId());
        return ResponseEntity.ok(comments);

    }
    //댓글 작성하기
    @PostMapping("/{categoryPath}/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable String categoryPath,
            @PathVariable Long postId,
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        CommentResponseDto response = commentService.createComment(postId, userDetails.getId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //댓글 삭제하기
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        commentService.deleteComment(commentId,userDetails.getId());

        return ResponseEntity.noContent().build();
    }

    //댓글 수정하기
   @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails){

       Long userId = userDetails.getId();
       CommentResponseDto response = commentService.updateComment(commentId, userId, dto);

       return ResponseEntity.ok(response);

   }

}
