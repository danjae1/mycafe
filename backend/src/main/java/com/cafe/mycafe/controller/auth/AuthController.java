package com.cafe.mycafe.controller.auth;

import com.cafe.mycafe.domain.dto.UserDto.JwtResponseDto;
import com.cafe.mycafe.domain.dto.UserDto.UserLoginRequestDto;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.UserRepository;
import com.cafe.mycafe.security.CustomUserDetails;
import com.cafe.mycafe.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody UserLoginRequestDto dto) {

        Authentication authentication;

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            authentication = authenticationManager.authenticate(authToken);

        } catch (BadCredentialsException e) {
            JwtResponseDto errorResponse = JwtResponseDto.builder()
                    .error("아이디 또는 비밀번호가 잘못되었습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        // 인증된 UserDetails에서 UserEntity 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserEntity user = userDetails.getUser(); // UserEntity 직접 접근 가능하도록 CustomUserDetails에 getUser() 추가

        // 토큰 발급 (userId, username 포함)
        String accessToken = jwtUtil.generateAccessToken(Map.of(), user);
        String refreshToken = jwtUtil.generateRefreshToken(Map.of(),user);

        Date expiration = jwtUtil.extractExpiration(accessToken);
        Long expiresIn = (expiration.getTime() - System.currentTimeMillis()) / 1000;

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        JwtResponseDto response = JwtResponseDto.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }


    //로그아웃시 토큰 삭제
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response){
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); //만료 시키기
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    //refreshToken httpOnly쿠키에 저장하기
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtil.resolveRefreshToken(request); // 쿠키에서 추출

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        String newAccessToken = jwtUtil.generateAccessToken(Map.of(),user);

        JwtResponseDto dto = JwtResponseDto.builder()
                .accessToken("Bearer " + newAccessToken)
                .expiresIn((jwtUtil.extractExpiration(newAccessToken).getTime() - System.currentTimeMillis()) / 1000)
                .build();

        return ResponseEntity.ok(dto);
    }
    // 새로고침했을 때 refreshToken 확인하고 유효하면 로그인상태 True반환하기
    @GetMapping("/check")
    public ResponseEntity<Map<String, Serializable>> checkLogin(@CookieValue(name = "refreshToken", required = false) String refreshToken){
        try {
            boolean isLoggedIn = false;
            if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                isLoggedIn = true;
            }
            return ResponseEntity.ok(Map.of("isLoggedIn", isLoggedIn));
        } catch (Exception e) {
            // 서버 에러 로깅
            e.printStackTrace();
            // 500 대신 200 + 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("isLoggedIn", false, "error", "서버 오류 발생"));
        }
    }

}
