package com.cafe.mycafe.controller.user;

import com.cafe.mycafe.domain.dto.UserDto.*;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.CommentRepository;
import com.cafe.mycafe.repository.PostRepository;
import com.cafe.mycafe.security.CustomUserDetails;
import com.cafe.mycafe.service.UserService;
import com.cafe.mycafe.service.UserServiceImpl;
import com.cafe.mycafe.util.JwtUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Builder
@RequestMapping
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final JwtUtil jwtUtil;


    @PostMapping("/signup")
    public ResponseEntity<UserSignUpRequestDto> signup(@RequestBody UserSignUpRequestDto dto){
        userService.signUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    //SideHeader에 들어갈 내 summary 정보
    @GetMapping("/users/me")
    public ResponseEntity<UserSummaryResponse> getMySummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        UserSummaryResponse response = userService.getMySummary(userId);

        return ResponseEntity.ok(response);
    }

    // 타 유저 클릭시 간략 정보
    @GetMapping("/users/{userId}/info")
    public ResponseEntity<UserSummaryResponse> getUserInfo(@PathVariable Long userId) {
        UserSummaryResponse response = userService.getMySummary(userId);
        return ResponseEntity.ok(response);
    }

    // 마이페이지 내 정보 조회
    @GetMapping("/users")
    public UserEntity getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.getUser(userDetails.getUser().getId());
    }

    // 내 정보 수정
    @PatchMapping("/users")
    public UserEntity updateUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @RequestBody UserEntity updatedUser) {
        return userService.updateUser(userDetails.getUser().getId(), updatedUser);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PasswordChangeRequest request){
        userService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다 !!");
    }

    // 회원 탈퇴
    @DeleteMapping("/users")
    public void deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUser(userDetails.getUser().getId());
    }

}
