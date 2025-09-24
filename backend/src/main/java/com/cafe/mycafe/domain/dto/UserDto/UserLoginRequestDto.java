package com.cafe.mycafe.domain.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequestDto {
    //로그인 요청시 필요한 정보
    private String username;
    private String password;
}
