package com.cafe.mycafe.controller.comment;

import com.cafe.mycafe.domain.dto.CommentDto.CommentListItemDto;
import com.cafe.mycafe.domain.dto.CommentDto.CommentRequestDto;
import com.cafe.mycafe.domain.dto.CommentDto.CommentResponseDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {

    private final CommentService commentService;

    // 마이페이지에서 내가 쓴 댓글 목록 조회
    @GetMapping("/me")
    public ResponseEntity<List<CommentListItemDto>> getMyComments(@AuthenticationPrincipal CustomUserDetails userDetails){

        Long userId = userDetails.getId();

        List<CommentListItemDto> comments = commentService.getCommentSummariesByUserId(userId, userId);

        return ResponseEntity.ok(comments);

    }

    // 다른 유저가 쓴 댓글 목록 조회
    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<List<CommentListItemDto>> getCommentsByUser(@PathVariable Long targetUserId,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();
        List<CommentListItemDto> comments = commentService.getCommentSummariesByUserId(targetUserId, userId);

        return ResponseEntity.ok(comments);
    }

    //게시글 조회시 댓글 목록 같이 불러오기
    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        List<CommentResponseDto> comments = commentService.getCommentsByPost(postId, userDetails.getId());
        return ResponseEntity.ok(comments);

    }
    //댓글 작성하기
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
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
