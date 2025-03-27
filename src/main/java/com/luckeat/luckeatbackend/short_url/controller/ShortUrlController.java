package com.luckeat.luckeatbackend.short_url.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.luckeat.luckeatbackend.short_url.service.ShortUrlService;

import lombok.RequiredArgsConstructor;

/**
 * 단축 URL 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    
    /**
     * 단축 URL 리다이렉트
     * 해시코드로 원본 URL로 리다이렉트합니다.
     */
    @GetMapping("/{hashCode}")
    public RedirectView redirectToOriginal(@PathVariable String hashCode) {
        String originalUrl = shortUrlService.getOriginalUrl(hashCode);
        return new RedirectView(originalUrl);
    }
}