package com.cafe.mycafe.service;

import com.cafe.mycafe.controller.exceptioncontroller.CategoryNotFoundException;
import com.cafe.mycafe.domain.dto.CommentDto.CommentResponseDto;
import com.cafe.mycafe.domain.dto.PostDto.*;
import com.cafe.mycafe.domain.dto.common.PageResult;
import com.cafe.mycafe.domain.entity.*;
import com.cafe.mycafe.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;
    private final PostViewRepository postViewRepository;

    @Transactional
    @Override
    public PostResponseDto createPost(Long userId, PostRequestDto dto, MultipartFile image) {
        String imageUrl = null;
        //게시글 작성할 때 이미지 파일이 업로드할 게 있다면
        if(image != null && !image.isEmpty()){
            //imageUrl = fileStorageService.save(image);
        }
        //UserEntity 가져와서 pk매핑된 user넣기
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        
        //카테고리 엔티티 가져오기
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new RuntimeException("존재하지 않는 카테고리입니다."));

        //카테고리 뷰, 좋아요
        PostEntity entity = PostEntity.builder()
                .category(category)
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .imageUrl(imageUrl)
                .viewCount(0L)
                .likeCount(0L)
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(entity);

        return PostResponseDto.builder()
                .id(entity.getId())
                .categoryId(entity.getCategory().getId())
                .userId(entity.getUser().getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getUser().getUsername())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Transactional
    @Override
    public void deletePost(Long postId, Long userId) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾지 못했습니다!"));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        post.setDeleted(true);
        postRepository.save(post);
    }

    @Transactional
    @Override
    public PostResponseDto updatePost(Long postId, Long userId, PostRequestDto dto) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(()-> new RuntimeException("게시글이 존재하지 않습니다."));

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리 입니다."));

        if (!post.getUser().getId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 글만 수정할 수 있습니다.");
        }

        if(dto.getCategoryId() != null) post.setCategory(category);
        if(dto.getTitle() != null) post.setTitle(dto.getTitle());
        if(dto.getContent()!= null) post.setContent(dto.getContent());
        //if(dto.getImageUrl() != null) post.setImageUrl(dto.getImageUrl());

        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .categoryId(post.getCategory().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getUser().getUsername())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                //updatedAt은 PostEntity에서 @Preupdate로 자동갱신
                .build();
    }
    
    //단일 게시물 조회
    @Override
    @Transactional
    public PostResponseDto getPostById(@PathVariable  Long postId, Long userId) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        UserEntity user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        }
        //TTL 1시간 부여하기
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        //1시간 내 조회기록 없으면 카운트 증가
        if (!postViewRepository.existsRecentView(postId,userId,oneHourAgo)){
            PostViewEntity view = PostViewEntity.builder()
                    .post(post)
                    .user(user)
                    .viewedAt(LocalDateTime.now())
                    .build();
            
            // 조회수 중복방지 했더니 동시성 문제 생김 그래서 db단에서 무시할 수 있도록 변경
            try {
                postViewRepository.save(view); // DB unique constraint로 안전하게 중복 방지
                post.setViewCount(post.getViewCount() + 1);
            } catch (DataIntegrityViolationException e) {
                // 이미 존재하면 무시
                System.out.println("이미 조회 기록이 존재합니다.");
            }
        }

        Long likeCount = postLikeRepository.countByPostId(postId);
        boolean likedByMe = postLikeRepository.existsByPostAndUser(post, user);

        //댓글 목록 조회
        List<CommentEntity> commentEntities = commentRepository.findByPost(post);

        List<Long> commentIds = commentEntities.stream()
                .map(CommentEntity :: getId)
                .toList();
        
        // 댓글 좋아요 갯수 한번에 조회
        Map<Long, Integer> likeCountMap = commentLikeService.getLikeCountForComments(commentIds);

        //유저가 좋아요 누른 댓글들 한 번에 조회
        List<Long> likedCommentIds = commentLikeService.getLikedCommentIdsByUser(userId);

        // 댓글 DTO로 변환하기
        List<CommentResponseDto> comments = commentEntities.stream()
                .map(c -> CommentResponseDto.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .userName(c.getUser().getUsername())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .deleted(c.isDeleted())
                        .likeCount(likeCountMap.getOrDefault(c.getId(), 0))
                        .likedByMe(likedCommentIds.contains(c.getId()))
                        .children(List.of()) // 대댓글 있으면 매핑
                        .build())
                .toList();

        // 최종 DTO를 반환하기
        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .writer(post.getUser().getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryId(post.getCategory().getId())
                .categoryName(post.getCategory().getName())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .likedByUser(likedByMe)
                .imageUrl(post.getImageUrl())
                .thumbnailUrl(post.getThumbnailUrl())
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Override
    public PageResult<PostListItemDto> getPostsByPath(String categoryPath, String keyword, int pageNum, int pageSize, Long currentUserId) {

        int startRow = (pageNum - 1) * pageSize + 1;
        int endRow = pageNum * pageSize;

        List<PostEntity> posts;
        int totalRow;

        String categoryPathNormalized = (categoryPath != null && !categoryPath.isEmpty())
                ? (categoryPath.startsWith("/") ? categoryPath : "/" + categoryPath)
                : null;

        if (categoryPathNormalized == null) {
            posts = postRepository.findPostsWithPaging(startRow, endRow);
            totalRow = postRepository.countAllByDeletedFalse();
        } else {
            Optional<CategoryEntity> optCategory = categoryRepository.findByPathAndDeletedFalse(categoryPathNormalized);
            if (optCategory.isEmpty()) {
                return PageResult.<PostListItemDto>builder()
                        .content(Collections.emptyList())
                        .pageNum(pageNum)
                        .pageSize(pageSize)
                        .totalRow(0)
                        .totalPageCount(0)
                        .startPageNum(0)
                        .endPageNum(0)
                        .build();
            }
            CategoryEntity category = optCategory.get();
            posts = postRepository.findByCategoryWithPaging(category.getId(), startRow, endRow);
            totalRow = postRepository.countByCategoryNative(category.getId());
        }

        List<PostListItemDto> items = posts.stream().map(post -> {
            boolean likedByUser = false;
            if (currentUserId != null) {
                likedByUser = postLikeRepository.existsByPostAndUser(
                        post,
                        UserEntity.builder().id(currentUserId).build()
                );
            }
            String writer = (post.getUser() != null) ? post.getUser().getUsername() : "알 수 없음";
            String categoryName = (post.getCategory() != null) ? post.getCategory().getName() : "카테고리 없음";

            return PostListItemDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .writer(writer)
                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .likedByUser(likedByUser)
                    .thumbnailUrl(post.getThumbnailUrl())
                    .categoryName(categoryName)
                    .createdAt(post.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        int totalPageCount = (int) Math.ceil((double) totalRow / pageSize);
        int startPageNum = Math.max(1, pageNum - 2);
        int endPageNum = Math.min(totalPageCount, pageNum + 2);

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





    @Override
    public PageResult<PostListItemDto> getPostSummariesByUserId(Long targetUserId, Long userId, int pageNum, int pageSize) {

        int startRow = (pageNum - 1) * pageSize + 1;
        int endRow = pageNum * pageSize;

        List<PostEntity> posts = postRepository.findPostsByUserWithPaging(targetUserId, startRow, endRow);
        int totalRow = postRepository.countPostsByUser(targetUserId);

        UserEntity user = Optional.ofNullable(userId)
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다.")))
                .orElse(null);

        List<PostListItemDto> items = posts.stream()
                .map(post -> PostListItemDto.builder()
                        .id(post.getId())
                        .writer(post.getUser().getUsername())
                        .title(post.getTitle())
                        .likeCount(post.getLikeCount())
                        .likedByUser(user != null && postLikeRepository.existsByPostAndUser(post, user))
                        .thumbnailUrl(post.getThumbnailUrl())
                        .viewCount(post.getViewCount())
                        .commentCount((long) post.getComments().size())
                        .categoryName(post.getCategory().getName())
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        int totalPageCount = (int) Math.ceil((double) totalRow / pageSize);
        int startPageNum = Math.max(1, pageNum - 2);
        int endPageNum = Math.min(totalPageCount, pageNum + 2);

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


    @Override
    public PageResult<PostListItemDto> getPostsCommentedByUser(Long targetUserId, Long userId, int pageNum, int pageSize) {
        int startRow = (pageNum - 1) * pageSize + 1;
        int endRow = pageNum * pageSize;

        List<PostEntity> posts = postRepository.findPostsCommentedByUserWithPaging(targetUserId, startRow, endRow);
        int totalRow = postRepository.countPostsCommentedByUser(targetUserId);

        Optional<UserEntity> currentUserOpt = (userId != null)
                ? userRepository.findById(userId)
                : Optional.empty();

        List<PostListItemDto> items = posts.stream()
                .map(post -> {
                    boolean likedByUser = currentUserOpt
                            .map(user -> postLikeRepository.existsByPostIdAndUserId(post.getId(), user.getId()))
                            .orElse(false);

                    return PostListItemDto.builder()
                            .id(post.getId())
                            .writer(post.getUser().getUsername())
                            .title(post.getTitle())
                            .viewCount(post.getViewCount())
                            .likeCount(post.getLikeCount())
                            .commentCount((long) post.getComments().size())
                            .thumbnailUrl(post.getThumbnailUrl())
                            .categoryName(post.getCategory().getName())
                            .createdAt(post.getCreatedAt())
                            .likedByUser(likedByUser)
                            .build();
                })
                .collect(Collectors.toList());

        int totalPageCount = (int) Math.ceil((double) totalRow / pageSize);
        int startPageNum = Math.max(1, pageNum - 2);
        int endPageNum = Math.min(totalPageCount, pageNum + 2);

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

    @Override
    public PageResult<PostListItemDto> getPostsLikedByUser(Long targetUserId, Long userId, int pageNum, int pageSize) {
        int startRow = (pageNum - 1) * pageSize + 1;
        int endRow = pageNum * pageSize;

        List<PostEntity> posts = postLikeRepository.findLikedPostsByUserWithPaging(targetUserId, startRow, endRow);
        int totalRow = postLikeRepository.countLikedPostsByUser(targetUserId);

        Optional<UserEntity> currentUserOpt = (userId != null)
                ? userRepository.findById(userId)
                : Optional.empty();

        List<PostListItemDto> items = posts.stream()
                .map(post -> {
                    boolean likedByUser = currentUserOpt
                            .map(user -> postLikeRepository.existsByPostIdAndUserId(post.getId(), user.getId()))
                            .orElse(false);

                    return PostListItemDto.builder()
                            .id(post.getId())
                            .writer(post.getUser().getUsername())
                            .title(post.getTitle())
                            .viewCount(post.getViewCount())
                            .likeCount(post.getLikeCount())
                            .commentCount((long) post.getComments().size())
                            .thumbnailUrl(post.getThumbnailUrl())
                            .categoryName(post.getCategory().getName())
                            .createdAt(post.getCreatedAt())
                            .likedByUser(likedByUser)
                            .build();
                })
                .collect(Collectors.toList());

        int totalPageCount = (int) Math.ceil((double) totalRow / pageSize);
        int startPageNum = Math.max(1, pageNum - 2);
        int endPageNum = Math.min(totalPageCount, pageNum + 2);

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
