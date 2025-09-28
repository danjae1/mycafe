package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.PostDto.*;
import com.cafe.mycafe.domain.entity.CategoryEntity;
import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.CategoryRepository;
import com.cafe.mycafe.repository.PostLikeRepository;
import com.cafe.mycafe.repository.PostRepository;
import com.cafe.mycafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

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
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(entity);

        return PostResponseDto.builder()
                .id(entity.getId())
                .categoryId(entity.getCategory().getId())
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
            throw new RuntimeException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        if(dto.getCategoryId() != null) post.setCategory(category);
        if(dto.getTitle() != null) post.setTitle(dto.getTitle());
        if(dto.getContent()!= null) post.setContent(dto.getContent());
        //if(dto.getImageUrl() != null) post.setImageUrl(dto.getImageUrl());

        return PostResponseDto.builder()
                .id(post.getId())
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
    public PostListResponse getPostById(Long postId, Long currentUserId) {
        return null;
    }
    
    //전체 게시글 조회
    @Override
    public PostListResponse getPosts(String categoryName, String keyword, int pageNum, int pageSize, Long currentUserId) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        List<PostEntity> posts;
        int totalRow;

        if (categoryName == null || categoryName.isEmpty()) {
            posts = postRepository.findAllByDeletedFalseOrderByCreatedAtDesc(pageable);
            totalRow = postRepository.countAllByDeletedFalse();
        } else {
            CategoryEntity category = categoryRepository.findByNameAndDeletedFalse(categoryName)
                    .orElseThrow(() -> new RuntimeException("카테고리가 없어요"));

            posts = postRepository.findAllByCategoryAndDeletedFalseOrderByCreatedAtDesc(category, pageable);
            totalRow = postRepository.countByCategory(category);
        }

        List<PostListItemDto> items = posts.stream().map(post -> {
            boolean likedByUser = false;
            if (currentUserId != null) {
                likedByUser = postLikeRepository.existsByPostAndUser(
                        post,
                        UserEntity.builder().id(currentUserId).build()
                );
            }
            return PostListItemDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .writer(post.getUser().getUsername())
                    .likeCount(post.getLikeCount())
                    .likedByUser(likedByUser)
                    .thumbnailUrl(post.getThumbnailUrl())
                    .categoryName(post.getCategory().getName())
                    .createdAt(post.getCreatedAt())
                    .build();

        }).collect(Collectors.toList());

        int totalPageCount = (int) Math.ceil((double) totalRow / pageSize);
        int startPageNum = Math.max(1, pageNum - 2);
        int endPageNum = Math.min(totalPageCount, pageNum + 2);

        PageResponse pageResponse = PageResponse.builder()
                .pageNum(pageNum)
                .totalRow(totalRow)
                .totalPageCount(totalPageCount)
                .startPageNum(startPageNum)
                .endPageNum(endPageNum)
                .build();

        return PostListResponse.builder()
                .list(items)
                .page(pageResponse)
                .build();
}
}
