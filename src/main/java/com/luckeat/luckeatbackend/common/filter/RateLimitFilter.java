package com.luckeat.luckeatbackend.common.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.function.Function;

/**
 * API Rate Limit 필터 
 * IP 주소 기반으로 각 클라이언트의 요청을 제한
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2) // IpAttributeFilter 다음에 실행
public class RateLimitFilter extends OncePerRequestFilter {

    private final Function<String, Bucket> bucketResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 클라이언트 식별자 획득 (IpAttributeFilter에서 설정한 값)
        String clientId = (String) request.getAttribute(IpAttributeFilter.IP_ADDRESS_ATTRIBUTE);
        
        if (clientId == null) {
            log.warn("클라이언트 IP 식별자가 없습니다. Rate Limit 적용 불가");
            filterChain.doFilter(request, response);
            return;
        }
        
        // 클라이언트 IP에 해당하는 버킷 가져오기
        Bucket bucket = bucketResolver.apply(clientId);
        
        // 토큰 사용 시도 (1개의 토큰 소비)
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        // 요청 허용 여부 확인
        if (probe.isConsumed()) {
            // 남은 요청 수를 응답 헤더에 포함
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // 요청 제한 초과 시 429 Too Many Requests 반환
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", 
                    String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests\",\"message\":\"API 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요.\"}");
            
            log.warn("Rate Limit 초과: 클라이언트 ID={}, URI={}", clientId, request.getRequestURI());
        }
    }

    /**
     * OPTIONS 요청과 /api/actuator/ 경로 요청은 Rate Limit에서 제외
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        return "OPTIONS".equals(method) || 
               uri.startsWith("/api/actuator/") || 
               uri.equals("/favicon.ico");
    }
} 