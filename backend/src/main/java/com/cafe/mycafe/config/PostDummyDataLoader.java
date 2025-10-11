package com.cafe.mycafe.config;

import com.cafe.mycafe.domain.entity.CategoryEntity;
import com.cafe.mycafe.domain.entity.PostEntity;
import com.cafe.mycafe.domain.entity.UserEntity;
import com.cafe.mycafe.repository.CategoryRepository;
import com.cafe.mycafe.repository.PostRepository;
import com.cafe.mycafe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PostDummyDataLoader implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public PostDummyDataLoader(PostRepository postRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 작성자: 첫 번째 사용자
        UserEntity user = userRepository.findById(1L).orElseThrow();

        // 카테고리별 더미 30개
        for (CategoryEntity category : categoryRepository.findAll()) {
            for (int i = 1; i <= 30; i++) {
                PostEntity post = PostEntity.builder()
                        .title(category.getName() + " 제목 " + i)
                        .content(category.getName() + " 내용 더미 " + i)
                        .writer(user.getUsername())
                        .user(user)
                        .category(category)
                        .viewCount(0L)
                        .deleted(false)
                        .build();
                postRepository.save(post);
            }
        }

        System.out.println("더미 게시글 삽입 완료!");
    }
}
