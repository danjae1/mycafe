package com.cafe.mycafe.domain.dto.CafeDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CafeSummaryResponse {

    private Long userCount;
    private Long postCount;
    private Long commentCount;

}
