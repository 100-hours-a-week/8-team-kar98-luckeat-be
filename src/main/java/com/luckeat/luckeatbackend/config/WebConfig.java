package com.luckeat.luckeatbackend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.luckeat.luckeatbackend.common.filter.IpAttributeFilter;

/**
 * 웹 관련 설정 클래스
 * - CORS 설정
 * - 기타 웹 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 필터 등록
     * 프론트엔드 애플리케이션에서의 API 접근 허용
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(getAllowedOrigins());
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setExposedHeaders(Arrays.asList("X-Rate-Limit-Remaining"));
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(new CorsFilter(source));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("corsFilter");
        return registration;
    }
    
    /**
     * IP 속성 필터 등록
     * Bucket4j에서 사용할 IP 주소 식별자를 설정
     */
    @Bean
    public FilterRegistrationBean<IpAttributeFilter> ipAttributeFilterRegistration(IpAttributeFilter ipAttributeFilter) {
        FilterRegistrationBean<IpAttributeFilter> registration = new FilterRegistrationBean<>(ipAttributeFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // CORS 필터 다음에 실행
        registration.addUrlPatterns("/*");
        registration.setName("ipAttributeFilter");
        return registration;
    }
    
    /**
     * 환경에 따른 허용 오리진 목록 반환
     */
    private List<String> getAllowedOrigins() {
        // 실제 프로덕션 환경에서는 구체적인 도메인 목록 설정
        return Arrays.asList(
            "https://dxa66rf338pjr.cloudfront.net",
            "https://luckeat.com",
            "http://localhost:3000"
        );
    }
} 