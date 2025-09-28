package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.PostDto.PostListItemDto;
import com.cafe.mycafe.domain.dto.PostDto.PostListResponse;
import com.cafe.mycafe.domain.dto.PostDto.PostRequestDto;
import com.cafe.mycafe.domain.dto.PostDto.PostResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    
    //게시글 작성
    public PostResponseDto createPost(Long userId, PostRequestDto dto, MultipartFile image);
    //게시글 삭제 
    public void deletePost(Long postId, Long userId);
    //게시글 수정
    public PostResponseDto updatePost(Long postId, Long userId,PostRequestDto dto);
    //게시글 id로 가져오기
    public PostListResponse getPostById(Long postId, Long currentUserId);
    //게시글 목록 불러오기
    public PostListResponse getPosts(String categoryName, String keyword,
                                     int pageNum, int pageSize, Long currentUserId);
}

