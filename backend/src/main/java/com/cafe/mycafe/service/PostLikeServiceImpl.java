package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.PostDto.PostLikeResponseDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListItemDto;
import com.cafe.mycafe.domain.dto.common.PageResult;
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

        // 변경된 좋아요 카운트를 데이터베이스에 영속화
        postRepository.save(post);

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

    // 유저가 좋아요를 누른 게시글 목록을 페이징 처리하여 조회
    @Override
    public PageResult<PostListItemDto> getLikedPostsByUser(Long userId, int pageNum, int pageSize) {
        // Oracle ROW_NUMBER()를 위한 시작/끝 행 번호 계산
        int startRow = (pageNum - 1) * pageSize + 1;
        int endRow = pageNum * pageSize;

        // 네이티브 쿼리로 좋아요 누른 시간 기준 내림차순 정렬된 게시글 조회
        List<PostEntity> likedPosts = postLikeRepository.findLikedPostsByUserWithPaging(userId, startRow, endRow);

        // 해당 유저가 좋아요를 누른 총 게시글 수 조회
        int totalRow = postLikeRepository.countLikedPostsByUser(userId);

        // PostEntity를 PostListItemDto로 변환
        List<PostListItemDto> items = likedPosts.stream()
                .map(post -> {
                    // LazyInitializationException 방지를 위해 댓글 수는 0으로 설정
                    // 필요시 별도 쿼리로 조회 가능
                    return PostListItemDto.builder()
                            .id(post.getId())
                            .writer(post.getUser() != null ? post.getUser().getUsername() : "알 수 없음")
                            .title(post.getTitle())
                            .likeCount(post.getLikeCount())
                            .likedByUser(true)  // 좋아요 누른 글 목록이므로 항상 true
                            .thumbnailUrl(post.getThumbnailUrl())
                            .viewCount(post.getViewCount())
                            .commentCount(0L)  // Lazy Loading 방지를 위해 0으로 설정
                            .categoryName(post.getCategory() != null ? post.getCategory().getName() : "카테고리 없음")
                            .createdAt(post.getCreatedAt())
                            .build();
                }).collect(Collectors.toList());

        // 전체 페이지 수와 페이지네이션 범위 계산
        int totalPageCount = (int) Math.ceil((double) totalRow / pageSize);
        int startPageNum = Math.max(1, pageNum - 2);
        int endPageNum = Math.min(totalPageCount, pageNum + 2);

        // 페이징 정보와 게시글 목록을 담은 결과 객체 생성
        return PageResult.<PostListItemDto>builder()
                .content(items)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalRow(totalRow)
                .totalPageCount(totalPageCount)
                .startPageNum(startPageNum)
                .endPageNum(endPageNum)
                .build();
    }

}
