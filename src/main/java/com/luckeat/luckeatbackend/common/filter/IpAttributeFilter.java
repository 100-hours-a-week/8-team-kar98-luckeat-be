package com.luckeat.luckeatbackend.common.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 클라이언트 IP 주소를 요청 속성으로 설정하는 필터
 * Bucket4j에서 사용하는 IP 기반 레이트 리밋을 위해 필요
 */
@Component
public class IpAttributeFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(IpAttributeFilter.class);
    public static final String IP_ADDRESS_ATTRIBUTE = "X-IP-ADDRESS";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // IP 주소와 User-Agent를 조합하여 고유 식별자로 사용
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String clientId = String.format("%s_%s", ipAddress, userAgent != null ? userAgent.hashCode() : "unknown");
        
        // 요청 속성에 IP 주소 기반 식별자 저장
        request.setAttribute(IP_ADDRESS_ATTRIBUTE, clientId);
        logger.debug("IP 속성 설정: {} = {}, URI: {}", IP_ADDRESS_ATTRIBUTE, clientId, request.getRequestURI());
        
        // HTTP 헤더로도 추가 (디버깅 용도)
        response.setHeader("X-Debug-ClientId", clientId);
        
        filterChain.doFilter(request, response);
    }
} 