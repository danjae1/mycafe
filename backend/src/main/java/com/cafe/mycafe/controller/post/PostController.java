package com.cafe.mycafe.controller.post;

import com.cafe.mycafe.domain.dto.PostDto.PostRequestDto;
import com.cafe.mycafe.domain.dto.PostDto.PostResponseDto;
import com.cafe.mycafe.security.CustomUserDetails;
import com.cafe.mycafe.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @PostMapping("/post")
    public ResponseEntity<PostResponseDto> createPost(@AuthenticationPrincipal CustomUserDetails user,
                                                      @RequestBody PostRequestDto dto
//                                                    ,@RequestPart(value = "image",required = false)
//                                                      MultipartFile image
){

        PostResponseDto response = postService.createPost(user.getId(),dto,null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id,
                                             @AuthenticationPrincipal CustomUserDetails user){

        postService.deletePost(id, user.getId());
        return ResponseEntity.ok("삭제되었습니다.");
    }
}
