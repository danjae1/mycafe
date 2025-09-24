package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.UserDto.UserSignUpRequestDto;

public interface UserService {
    public void signUp(UserSignUpRequestDto dto);
}
