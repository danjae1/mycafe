package com.cafe.mycafe.controller.auth;

import com.cafe.mycafe.domain.dto.UserDto.JwtResponseDto;
import com.cafe.mycafe.domain.dto.UserDto.UserLoginRequestDto;
import com.cafe.mycafe.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody UserLoginRequestDto dto) {

        Authentication authentication;

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            
            //JWT발급 전 1회용 인증
            //token 넣으면 내부적으로 UserDetailService + PasswordENcode를 이용해서 인증처리가 된다.
            authentication = authenticationManager.authenticate(authToken);

        } catch (BadCredentialsException e) {
            JwtResponseDto errorResponse = JwtResponseDto.builder()
                    .error("아이디 또는 비밀번호가 잘못되었습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        //1회용 인증이 성공하면,
        // 인증된 사용자명
        String username = authentication.getName();

        // 토큰 발급하기
        String accessToken = jwtUtil.generateAccessToken(username,Map.of());
        String refreshToken = jwtUtil.generateRefreshToken(username,Map.of());
        Date expiration = jwtUtil.extractExpiration(accessToken); // 남은 만료시간 초단위로 바꿔주기
        Long expiresIn = (expiration.getTime() - System.currentTimeMillis()) / 1000 ;

        //응답에 토큰들과 만료시간을 넣고 응답한다.
        JwtResponseDto response = JwtResponseDto.builder()
                .accessToken("Bearer "+accessToken)
                .refreshToken("Bearer "+refreshToken)
                .expiresIn(expiresIn)
                .build();

        return ResponseEntity.ok(response);

    }
}
