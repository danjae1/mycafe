package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.CommentDto.CommentLikeResponseDto;
import com.cafe.mycafe.domain.dto.PostDto.PostLikeResponseDto;
import com.cafe.mycafe.domain.entity.*;
import com.cafe.mycafe.repository.CommentLikeRepository;
import com.cafe.mycafe.repository.CommentRepository;
import com.cafe.mycafe.repository.PostRepository;
import com.cafe.mycafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements  CommentLikeService{

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentLikeResponseDto toggleLike(Long commentId, Long userId) {

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Optional<CommentLikeEntity> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);

        boolean liked;

        if(existingLike.isPresent()){
            // 좋아요 취소
            commentLikeRepository.delete(existingLike.get());
            comment.decreaseLikeCount(); // likeCount 감소
            liked = false;
        } else{
            // 좋아요 추가
            CommentLikeEntity newLike = CommentLikeEntity.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(newLike);
            comment.increaseLikeCount(); // likeCount 증가
            liked = true;
        }

        return CommentLikeResponseDto.builder()
                .commentId(comment.getId())
                .likeCount(comment.getLikeCount())
                .likedByMe(liked)
                .build();
    }

    @Override
    //단일 게시글에서 UI표시용 댓글 좋아요 여부를 확인한다
    public boolean isLikeByUser(Long commentId, Long userId) {

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        return commentLikeRepository.existsByCommentAndUser(comment, user);
    }

    @Override
    //마이페이지용 내가 좋아요 누른 글 확인하기
    public List<Long> getLikedCommentIdsByUser(Long userId) {


        return commentLikeRepository.findAllByUserId(userId)
                .stream()
                .map(like -> like.getComment().getId())
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Integer> getLikeCountForComments(List<Long> commentIds) {
        
        //게시글의 댓글 별 좋아요수 한 번에 조회
        List<Object[]> counts = commentLikeRepository.countLikesByCommentIds(commentIds);
        
        //object-> map<long,integer> 변환하기
        Map<Long, Integer> likeCountMap = new HashMap<>();
        for(Object[] row : counts){
            Long commentId = (Long) row[0];
            Long count = (Long) row[1];
            likeCountMap.put(commentId, count.intValue());
        }


        return likeCountMap;
        
    }

}