package com.cafe.mycafe.controller.cafe;

import com.cafe.mycafe.domain.dto.CafeDto.CafeSummaryResponse;
import com.cafe.mycafe.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CafeController {

    private final CafeService cafeService;

    @GetMapping("/cafe/summary")
    public ResponseEntity<CafeSummaryResponse> getCafeSummary(){
        return ResponseEntity.ok(cafeService.getCafeSummary());
    }


}
