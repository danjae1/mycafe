package com.cafe.mycafe.domain.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponseDto {
    //access refresh 구조
    private String accessToken; //발급도니 Access Token
    @Builder.Default
    private String tokenType = "Bearer"; //토큰 타입 비어러가 관례라고 한다
    private Long expiresIn; //Access Token 만료 시간(초 단위)
    
    private String error; //에러 메세지용
}
