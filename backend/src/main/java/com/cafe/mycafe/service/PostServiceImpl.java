package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.PostDto.PostDto;
import com.cafe.mycafe.domain.dto.PostDto.PostRequestDto;
import com.cafe.mycafe.domain.dto.PostDto.PostResponseDto;
import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.PostRepository;
import com.cafe.mycafe.repository.UserRepository;
import com.cafe.mycafe.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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

        //카테고리 뷰, 좋아요
        PostEntity entity = PostEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(entity);

        return PostResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .author(entity.getUser().getUsername())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }

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

    @Override
    public void updatePost() {

    }

    @Override
    public PostDto getPostDetail() {
        return null;
    }
}
