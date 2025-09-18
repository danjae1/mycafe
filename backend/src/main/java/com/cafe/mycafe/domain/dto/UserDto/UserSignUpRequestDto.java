package com.example.naver_cafe.domain.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequestDto {
    private String username;
    private String password;
    private String email;
    private String grade; // 브론즈 실버 골드

}
