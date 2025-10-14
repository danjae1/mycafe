package com.cafe.mycafe.service;

import com.cafe.mycafe.domain.dto.CafeDto.CafeSummaryResponse;
import org.springframework.http.ResponseEntity;

public interface CafeService {

    CafeSummaryResponse getCafeSummary();
}
