package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.CafeDto.CafeSummaryResponse;
import com.cafe.mycafe.repository.CommentRepository;
import com.cafe.mycafe.repository.PostRepository;
import com.cafe.mycafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CafeServiceImpl implements CafeService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public CafeSummaryResponse getCafeSummary() {
        long userCount = userRepository.count();
        long postCount = postRepository.count();
        long commentCount = commentRepository.count();

        return new CafeSummaryResponse(userCount, postCount, commentCount);
    }
}
