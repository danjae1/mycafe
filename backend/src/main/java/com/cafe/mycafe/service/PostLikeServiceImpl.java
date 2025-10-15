package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.PostDto.PostLikeResponseDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListItemDto;
import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.PostLikeEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.PostLikeRepository;
import com.cafe.mycafe.repository.PostRepository;
import com.cafe.mycafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService{

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    @Override
    public PostLikeResponseDto toggleLike(Long postId, Long userId) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Optional<PostLikeEntity> existingLike = postLikeRepository.findByPostAndUser(post, user);

        boolean liked;

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            liked = false;
            // 좋아요 취소

        } else {

            PostLikeEntity like = PostLikeEntity.builder()
                    .post(post)
                    .user(user)
                    .build();

            postLikeRepository.save(like);
            post.increaseLikeCount();
            liked = true;

        }

        return new PostLikeResponseDto(liked, post.getLikeCount());
    }


    @Override
    //단일 게시글 UI표시용 좋아요 여부를 확인한다.
    public boolean isLikeByUser(Long postId, Long userId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return postLikeRepository.existsByPostAndUser(post, user);
    }

    @Override
    //마이페이지용 내가 좋아요 누른 글 확인하기
    public List<PostListItemDto> getLikedPostsByUser(Long userId) {

        List<PostEntity> posts = postLikeRepository.findLikedPostsByUser(userId);

        return posts.stream()
                .map(post -> PostListItemDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .writer(post.getWriter())
                        .createdAt(post.getCreatedAt())
                        .viewCount(post.getViewCount())
                        .categoryName(post.getCategory().getName())
                        .commentCount((long) post.getComments().size())
                        .likeCount(post.getLikeCount())
                        .build())
                .collect(Collectors.toList());
    }
}
