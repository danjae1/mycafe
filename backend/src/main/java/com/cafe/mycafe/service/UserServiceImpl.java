package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.UserDto.UserSignUpRequestDto;
import com.cafe.mycafe.domain.entity.Grade;
import com.cafe.mycafe.domain.entity.Role;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Transactional
    @Override
    public void signUp(UserSignUpRequestDto dto){

        //비밀번호 암호화하기
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // DTO를 Entity 객체로 변환하고 기본값 넣기
        UserEntity user = UserEntity.builder()
                .username(dto.getUsername())
                .password(encodedPassword)
                .email(dto.getEmail())
                .role(Role.USER)
                .grade(Grade.BRONZE)
                .joinDate(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }


}
