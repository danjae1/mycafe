package com.cafe.mycafe.domain.dto.PostDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class PostRequestDto {
    
    private String title;
    private String content;

    //작성자는 jwt에서 뺴서 쓸 수 있음
}
