package com.cafe.mycafe.domain.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequestDto {
    //회원가입 요청시 필요한 정보
    private String username;
    private String password;
    private String email;
    private String grade; // 브론즈 실버 골드


}
