package com.cafe.mycafe.domain.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshRequestDto {
    //AccessToken 만료시 Refresh Token으로 재발급 요청할 때 사용하는 거
    private String refreshToken; //클라이언트에서 보내는 Refresh Token
}
