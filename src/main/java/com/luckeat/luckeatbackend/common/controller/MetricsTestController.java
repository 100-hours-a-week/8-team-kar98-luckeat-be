package com.luckeat.luckeatbackend.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

/**
 * 메트릭 테스트를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/metrics-test")
public class MetricsTestController {

    private final Counter reservationCounter;
    private final Counter reviewCounter;
    private final Timer apiRequestTimer;

    @Autowired
    public MetricsTestController(Counter reservationCounter, Counter reviewCounter, Timer apiRequestTimer) {
        this.reservationCounter = reservationCounter;
        this.reviewCounter = reviewCounter;
        this.apiRequestTimer = apiRequestTimer;
    }

    /**
     * 예약 카운터 증가
     */
    @GetMapping("/increment-reservations")
    public ResponseEntity<Map<String, Object>> incrementReservations() {
        reservationCounter.increment();
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        response.put("message", "예약 카운터가 증가되었습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰 카운터 증가
     */
    @GetMapping("/increment-reviews")
    public ResponseEntity<Map<String, Object>> incrementReviews() {
        reviewCounter.increment();
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        response.put("message", "리뷰 카운터가 증가되었습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * API 요청 타이머 테스트
     */
    @GetMapping("/test-timer")
    public ResponseEntity<Map<String, Object>> testTimer() throws InterruptedException {
        Timer.Sample sample = Timer.start();
        
        // 임의의 작업 시뮬레이션
        Thread.sleep(500);
        
        sample.stop(apiRequestTimer);
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        response.put("message", "API 요청 타이머 테스트가 완료되었습니다.");
        
        return ResponseEntity.ok(response);
    }
} 