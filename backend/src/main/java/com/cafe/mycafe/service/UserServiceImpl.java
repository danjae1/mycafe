package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.UserDto.UserSignUpRequestDto;
import com.cafe.mycafe.domain.dto.UserDto.UserSummaryResponse;
import com.cafe.mycafe.domain.entity.Grade;
import com.cafe.mycafe.domain.entity.Role;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.CommentRepository;
import com.cafe.mycafe.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserSummaryResponse getMySummary(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        int postCount = postRepository.countByUserId(userId);
        int commentCount = commentRepository.countByUserId(userId);

        return UserSummaryResponse.builder()
                .userId(user.getId())
                .postCount(postCount)
                .commentCount(commentCount)
                .grade(user.getGrade())
                .createdAt(user.getJoinDate())
                .build();
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

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
