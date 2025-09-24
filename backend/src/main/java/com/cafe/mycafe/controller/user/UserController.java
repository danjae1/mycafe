package com.cafe.mycafe.controller.user;

import com.cafe.mycafe.domain.dto.UserDto.JwtResponseDto;
import com.cafe.mycafe.domain.dto.UserDto.TokenRefreshResponseDto;
import com.cafe.mycafe.domain.dto.UserDto.UserLoginRequestDto;
import com.cafe.mycafe.domain.dto.UserDto.UserSignUpRequestDto;
import com.cafe.mycafe.service.UserService;
import com.cafe.mycafe.service.UserServiceImpl;
import com.cafe.mycafe.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    public final AuthenticationManager authManager;

    @PostMapping("/user")
    public ResponseEntity<UserSignUpRequestDto> signup(@RequestBody UserSignUpRequestDto dto){
        userService.signUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



}
