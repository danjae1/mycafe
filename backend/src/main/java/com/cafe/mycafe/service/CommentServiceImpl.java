package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.CommentDto.CommentListItemDto;
import com.cafe.mycafe.domain.dto.CommentDto.CommentRequestDto;
import com.cafe.mycafe.domain.dto.CommentDto.CommentResponseDto;
import com.cafe.mycafe.domain.entity.CommentEntity;
import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.CommentLikeRepository;
import com.cafe.mycafe.repository.CommentRepository;
import com.cafe.mycafe.repository.PostRepository;
import com.cafe.mycafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements  CommentService{

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Override
    public CommentResponseDto createComment(Long postId, Long userId, CommentRequestDto dto) {
        // 댓글 달 게시글 조회하기
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 유저 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 대댓글일 경우 부모 댓글 있는지 조회하기
        CommentEntity parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
        }

        // 댓글 쓰고
        CommentEntity comment = CommentEntity.builder()
                .content(dto.getContent())
                .post(post)
                .user(user)
                .parent(parent)
                .deleted(false)
                .build();

        //저장해주고
        commentRepository.save(comment);

        // 5. DTO 반환 (좋아요 관련은 없으므로 0 / false)
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userName(user.getUsername())
                .createdAt(comment.getCreatedAt())
                .deleted(false)
                .likeCount(0)
                .likedByMe(false)
                .children(List.of())
                .build();
    }

    @Override
    public List<CommentResponseDto> getCommentsByPost(Long postId, Long userId) {
        //게시글 댓글 조회
        List<CommentEntity> comments = commentRepository.findAllByPostIdAndDeletedFalse(postId);

        //  댓글 ID 추출
        List<Long> commentIds = comments.stream().map(CommentEntity::getId).toList();

        // 좋아요 개수 Map
        Map<Long, Long> likeCountMap = commentLikeRepository.countLikesByCommentIds(commentIds)
                .stream().collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Long) obj[1]));

        // 내가 좋아요 눌렀는지 여부 Map
        Set<Long> likedSet = new HashSet<>(commentLikeRepository.findLikedCommentIdsByUser(commentIds, userId));
        Map<Long, Boolean> likedByMeMap = commentIds.stream()
                .collect(Collectors.toMap(id -> id, likedSet::contains));

        // 최상위 댓글만 DTO로 변환 (재귀적으로 children 처리)
        return comments.stream()
                .filter(c -> c.getParent() == null)
                .map(c -> toDto(c, likeCountMap, likedByMeMap))
                .toList();
    }
    
    //댓글 엔티티  dto로 반환하기
    private CommentResponseDto toDto(CommentEntity comment,
                                     Map<Long, Long> likeCountMap,
                                     Map<Long, Boolean> likedByMeMap) {

        long likeCount = likeCountMap.getOrDefault(comment.getId(), 0L);
        boolean likedByMe = likedByMeMap.getOrDefault(comment.getId(), false);

        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .userName(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .deleted(comment.isDeleted())
                .likeCount((int) likeCount)
                .likedByMe(likedByMe)
                .children(comment.getChildren() == null ? List.of() :
                        comment.getChildren().stream()
                                .filter(c -> !c.isDeleted()) // 삭제된 댓글 제외
                                .map(c -> toDto(c, likeCountMap, likedByMeMap)) // 재귀 호출
                                .toList())
                .build();
    }

    @Override
    public CommentResponseDto updateComment(Long commentId, Long userId, CommentRequestDto dto) {

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if(!comment.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        if(comment.isDeleted()){
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.setContent(dto.getContent());
        commentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userName(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .deleted(comment.isDeleted())
                .likeCount(0)
                .likedByMe(false)
                .children(comment.getChildren() == null ? List.of() :
                        comment.getChildren().stream()
                                .map(c -> toDto(c, Map.of(), Map.of())) // children DTO 초기화
                                .toList())
                .build();
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if(!comment.getUser().getUsername().equals(userId)){
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        if(comment.isDeleted()){
            throw new IllegalStateException("댓글 작성자만 삭제할 수 있습니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }
    
    // targetUser가 쓴 댓글 목록 불러오기
    @Override
    public List<CommentListItemDto> getCommentSummariesByUserId(Long targetUserId, Long userId) {
        
        //targetUserId가 쓴 댓글 목록
        List<CommentEntity> comments = commentRepository.findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc(targetUserId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        return comments.stream()
                .map(comment -> CommentListItemDto.builder()
                        .id(comment.getId())
                        .writer(comment.getUser().getUsername())
                        .content(comment.getContent())
                        .postTitle(comment.getPost().getTitle())
                        .createdAt(comment.getCreatedAt())
                        .likeCount(comment.getLikeCount())
                        .likedByUser(commentLikeRepository.existsByCommentAndUser(comment, user)
                        )
                        .build())
                .collect(Collectors.toList());
    }
}
