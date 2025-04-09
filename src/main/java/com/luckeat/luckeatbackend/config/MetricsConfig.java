package com.luckeat.luckeatbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Prometheus 메트릭 설정을 위한 설정 클래스
 */
@Configuration
public class MetricsConfig {

    private final MeterRegistry meterRegistry;

    @Autowired
    public MetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // 예약 카운터
    @Bean
    public Counter reservationCounter() {
        return Counter.builder("luckeat.reservations.count")
                .description("예약 총 건수")
                .register(meterRegistry);
    }

    // 리뷰 카운터
    @Bean
    public Counter reviewCounter() {
        return Counter.builder("luckeat.reviews.count")
                .description("리뷰 총 건수")
                .register(meterRegistry);
    }

    // API 요청 타이머
    @Bean
    public Timer apiRequestTimer() {
        return Timer.builder("luckeat.api.request.duration")
                .description("API 요청 처리 시간")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }
} 