package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.UserDto.UserSignUpRequestDto;
import com.cafe.mycafe.domain.dto.UserDto.UserSummaryResponse;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.UserRepository;

public interface UserService {

    public void signUp(UserSignUpRequestDto dto);
    public UserSummaryResponse getMySummary(Long userId);
    public UserEntity findById(Long id);
}
