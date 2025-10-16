package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.UserDto.PasswordChangeRequest;
import com.cafe.mycafe.domain.dto.UserDto.UserSignUpRequestDto;
import com.cafe.mycafe.domain.dto.UserDto.UserSummaryResponse;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.UserRepository;

public interface UserService {

    public void signUp(UserSignUpRequestDto dto);
    public UserEntity getUser(Long id);
    public UserEntity updateUser(Long id, UserEntity updatedUser);
    public void changePassword(Long id, PasswordChangeRequest request);
    public void deleteUser(Long id);

    public UserSummaryResponse getMySummary(Long userId);
    //public UserEntity findById(Long id);


}
