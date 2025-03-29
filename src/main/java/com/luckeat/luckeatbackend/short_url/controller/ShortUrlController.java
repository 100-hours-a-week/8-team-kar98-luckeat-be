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
        
        // 콘솔에 사용자 정보 로깅
        System.out.println("단축 URL 클릭 정보 ----");
        System.out.println("해시코드: " + hashCode);
        System.out.println("IP 주소: " + ipAddress);
        System.out.println("브라우저/기기 정보: " + userAgent);
        System.out.println("클릭 시간: " + clickTime);
        System.out.println("리퍼러: " + (referer != null ? referer : "직접 접속"));
        System.out.println("세션 ID: " + request.getSession().getId());
        System.out.println("----------------------");
        
        String originalUrl = shortUrlService.getOriginalUrl(hashCode);
        return new RedirectView(originalUrl);
    }
}