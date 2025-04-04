package com.luckeat.luckeatbackend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * API Rate Limit 설정 클래스
 * Bucket4j를 사용하여 IP 주소 기반 요청 제한 구현
 */
@Configuration
public class RateLimitConfig {

    // 기본 제한 설정 (분당 요청 수)
    @Value("${rate-limit.default-limit:1000}")
    private int defaultLimit;

    // 시간 윈도우 (분)
    @Value("${rate-limit.window-minutes:1}")
    private int windowMinutes;

    // 인증된 사용자에 대한 제한 (분당 요청 수)
    @Value("${rate-limit.authenticated-limit:2000}")
    private int authenticatedLimit;
    
    // 버킷 캐시 (클라이언트 ID → 버킷)
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    
    /**
     * 버킷 해결자
     * 클라이언트 IP를 기반으로 적절한 버킷 생성
     */
    @Bean
    public Function<String, Bucket> bucketResolver() {
        return clientId -> {
            // 현재 인증 정보 확인 
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && 
                                    !authentication.getName().equals("anonymousUser");

            // 인증 여부에 따라 다른 제한 적용
            int limit = isAuthenticated ? authenticatedLimit : defaultLimit;
            
            // 캐시에서 버킷 찾기, 없으면 생성
            return bucketCache.computeIfAbsent(clientId, id -> {
                Bandwidth bandwidth = Bandwidth.classic(limit, Refill.intervally(limit, Duration.ofMinutes(windowMinutes)));
                return Bucket.builder().addLimit(bandwidth).build();
            });
        };
    }
} 