package com.luckeat.luckeatbackend.short_url.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.luckeat.luckeatbackend.short_url.service.ShortUrlService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 단축 URL 컨트롤러
 */
@RestController
@RequestMapping("/s")
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    
    /**
     * 단축 URL 리다이렉트
     * 해시코드로 원본 URL로 리다이렉트합니다.
     */
    @GetMapping("/{hashCode}")
    public RedirectView redirectToOriginal(@PathVariable String hashCode, HttpServletRequest request) {
        // 사용자 요청 정보 로깅
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        LocalDateTime clickTime = LocalDateTime.now();
        
        // 콘솔에 사용자 정보 로깅 (최대한 많은 정보)
        System.out.println("단축 URL 클릭 정보 ----");
        System.out.println("해시코드: " + hashCode);
        System.out.println("IP 주소: " + ipAddress);
        System.out.println("브라우저/기기 정보: " + userAgent);
        System.out.println("클릭 시간: " + clickTime);
        System.out.println("리퍼러: " + (referer != null ? referer : "직접 접속"));
        System.out.println("세션 ID: " + request.getSession().getId());
        System.out.println("요청 URL: " + request.getRequestURL());
        System.out.println("요청 URI: " + request.getRequestURI());
        System.out.println("쿼리스트링: " + request.getQueryString());
        System.out.println("HTTP 메서드: " + request.getMethod());
        System.out.println("컨텍스트 경로: " + request.getContextPath());
        System.out.println("서블릿 경로: " + request.getServletPath());
        System.out.println("Path Info: " + request.getPathInfo());
        System.out.println("Path Translated: " + request.getPathTranslated());
        System.out.println("Remote Host: " + request.getRemoteHost());
        System.out.println("Remote Port: " + request.getRemotePort());
        System.out.println("Local Addr: " + request.getLocalAddr());
        System.out.println("Local Name: " + request.getLocalName());
        System.out.println("Local Port: " + request.getLocalPort());
        System.out.println("Requested Session ID: " + request.getRequestedSessionId());
        System.out.println("Session ID 유효: " + request.isRequestedSessionIdValid());
        System.out.println("Session ID 쿠키: " + request.isRequestedSessionIdFromCookie());
        System.out.println("Session ID URL: " + request.isRequestedSessionIdFromURL());
        System.out.println("Remote User: " + request.getRemoteUser());
        System.out.println("User Principal: " + (request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null));
        // 쿠키 정보
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                System.out.println("쿠키: " + cookie.getName() + "=" + cookie.getValue());
            }
        }
        // 모든 헤더 정보
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println("헤더: " + headerName + " = " + request.getHeader(headerName));
        }
        // 모든 파라미터 정보
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            System.out.println("파라미터: " + paramName + " = " + request.getParameter(paramName));
        }
        System.out.println("----------------------");
        
        String originalUrl = shortUrlService.getOriginalUrl(hashCode);
        return new RedirectView(originalUrl);
    }
}