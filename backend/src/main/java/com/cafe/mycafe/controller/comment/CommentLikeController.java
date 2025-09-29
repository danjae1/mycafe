package com.cafe.mycafe.controller.comment;

import com.cafe.mycafe.security.CustomUserDetails;
import com.cafe.mycafe.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    
    //댓글에 좋아요 추가+취소
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable Long commentId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentLikeService.toggleLike(commentId, userDetails.getId());
        return ResponseEntity.ok().build();
    }
    
    //특정 댓글에 내가 좋아요 눌렀는지 여부 확인 ui용
    @GetMapping("/comments/{commentId}/likes/me")
    public ResponseEntity<Boolean> isLikedByMe(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean liked = commentLikeService.isLikeByUser(commentId, userDetails.getId());
        return ResponseEntity.ok(liked);
    }

    //타 유저가 좋아요 누른 댓글 목록 가져오기
    @GetMapping("/comments/liked")
    public ResponseEntity<List<Long>> getLikedCommentsByUser(@PathVariable Long userId) {
        List<Long> likedCommentIds = commentLikeService.getLikedCommentIdsByUser(userId);

        return ResponseEntity.ok(likedCommentIds);
    }

    //마이페지이용 내가 좋아요 누른 댓글 목록 가져오기
    @GetMapping("/comments/liked")
    public ResponseEntity<List<Long>> getLikedCommentsByMe(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();

        List<Long> likedCommentIds = commentLikeService.getLikedCommentIdsByUser(userId);

        return ResponseEntity.ok(likedCommentIds);
    }
    //댓글 목록 불러올 때 좋아요갯수 한 번에 불러오기
    @PostMapping("/comments/likes/counts")
    public ResponseEntity<Map<Long, Integer>> getLikeCounts(@RequestBody List<Long> commentIds) {
        Map<Long, Integer> likeCounts = commentLikeService.getLikeCountForComments(commentIds);
        return ResponseEntity.ok(likeCounts);
    }
}
