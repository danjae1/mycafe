package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.PostDto.PostDto;
import com.cafe.mycafe.domain.dto.PostDto.PostRequestDto;
import com.cafe.mycafe.domain.dto.PostDto.PostResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {

    public PostResponseDto createPost(Long userId, PostRequestDto dto, MultipartFile image);
    public void deletePost(Long postId, Long userId);
    public void updatePost();
    public PostDto getPostDetail();

}

