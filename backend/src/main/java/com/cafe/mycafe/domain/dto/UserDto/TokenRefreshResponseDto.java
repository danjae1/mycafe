package com.cafe.mycafe.domain.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TokenRefreshResponseDto {
    //Refresh Token 요청 성공했을 때 새로운  Access Token과 Refresh Token 응답하기용
    private String accessToken; // 새로 발급된 AccessToken
    private String refreshToken; // 필요에 따라 새로 발급된 RefreshToken
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn; //Access Token 만료시간
}
