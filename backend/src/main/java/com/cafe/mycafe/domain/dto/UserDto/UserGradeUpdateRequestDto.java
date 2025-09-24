package com.cafe.mycafe.domain.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGradeUpdateRequestDto {
    //등업할 때 필요한 정보
    private Long userId; //사용자 특정하기 위한 고유ID
    private String grade;
}
