package com.luckeat.luckeatbackend.common.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 모든 HTTP 요청과 응답을 로깅하는 필터
 * 요청 시작/종료 시간, 처리 시간, 요청 정보, 응답 상태 등을 기록
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("API_LOGGER");
    private static final String REQUEST_ID = "requestId";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 요청 ID 생성 및 MDC에 저장
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(REQUEST_ID, requestId);
        
        // 캐싱 래퍼로 요청/응답 감싸기
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        // 시작 시간 기록
        LocalDateTime startTime = LocalDateTime.now();
        String formattedStartTime = startTime.format(formatter);
        
        try {
            // 요청 시작 로그
            logRequest(requestWrapper, requestId, formattedStartTime);
            
            // 요청 처리 진행
            filterChain.doFilter(requestWrapper, responseWrapper);
            
            // 종료 시간 및 처리 시간 계산
            LocalDateTime endTime = LocalDateTime.now();
            long duration = java.time.Duration.between(startTime, endTime).toMillis();
            
            // 응답 로그
            logResponse(responseWrapper, requestId, endTime.format(formatter), duration);
            
            // 응답 내용을 클라이언트에게 전송하기 위해 복사본 생성
            responseWrapper.copyBodyToResponse();
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }
    
    private void logRequest(ContentCachingRequestWrapper request, String requestId, String startTime) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = uri + (queryString != null ? "?" + queryString : "");
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        log.info("[{}] {} | 시작: {} | 요청: {} {} | IP: {} | User-Agent: {}", 
                requestId, 
                "요청",
                startTime, 
                method, 
                fullPath, 
                remoteAddr, 
                userAgent);
    }
    
    private void logResponse(ContentCachingResponseWrapper response, String requestId, String endTime, long duration) {
        int status = response.getStatus();
        String statusText = getStatusText(status);
        
        log.info("[{}] {} | 종료: {} | 응답: {} ({}) | 처리시간: {}ms", 
                requestId, 
                "응답",
                endTime, 
                status, 
                statusText, 
                duration);
    }
    
    private String getStatusText(int status) {
        if (status >= 200 && status < 300) {
            return "성공";
        } else if (status >= 300 && status < 400) {
            return "리다이렉트";
        } else if (status >= 400 && status < 500) {
            return "클라이언트 오류";
        } else if (status >= 500) {
            return "서버 오류";
        } else {
            return "알 수 없음";
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 정적 리소스나 특정 경로는 로깅에서 제외
        return path.contains("/actuator") || 
               path.contains("/swagger") ||
               path.contains("/v3/api-docs") ||
               path.endsWith(".ico") ||
               path.endsWith(".css") ||
               path.endsWith(".js") ||
               path.endsWith(".png") ||
               path.endsWith(".jpg") ||
               path.endsWith(".gif");
    }
} 