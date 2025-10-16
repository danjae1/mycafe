package com.cafe.mycafe.domain.dto.UserDto;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    
    private String currentPassword; //기존 비밀번호 
    private String newPassword; // 새 비밀번호
    private String confirmPassword; // 새 비밀번호 확인
    
}
