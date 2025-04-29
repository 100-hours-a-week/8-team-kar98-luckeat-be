package com.luckeat.luckeatbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.luckeat.luckeatbackend.security.jwt.JwtAuthenticationFilter;
import com.luckeat.luckeatbackend.security.jwt.JwtTokenProvider;
import com.luckeat.luckeatbackend.users.service.JwtBlacklistService;

/**
 * 보안 관련 설정 클래스
 * - Spring Security 설정
 * - JWT 인증 설정
 * - 인증/인가 규칙 설정
 * - 암호화 설정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtBlacklistService jwtBlacklistService;

	public SecurityConfig(JwtTokenProvider jwtTokenProvider, JwtBlacklistService jwtBlacklistService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.jwtBlacklistService = jwtBlacklistService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// CSRF 비활성화, CORS 설정 적용
		http.csrf(AbstractHttpConfigurer::disable)
			.cors(AbstractHttpConfigurer::disable) // WebConfig에서 이미 CORS Filter를 등록함
			// 세션 상태가 없는 RESTful API 설정
			.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			// URL 기반 인증/인가 규칙 설정
			.authorizeHttpRequests(authorize -> authorize
				// 공개 엔드포인트 (인증 불필요)
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/api/actuator/**").permitAll()
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/api/users/signup").permitAll()
				.requestMatchers("/api/users/login").permitAll()
				.requestMatchers("/api/v1/users/login").permitAll()
				.requestMatchers("/api/v1/users/register").permitAll()
				.requestMatchers("/api/metrics-test/**").permitAll() // 메트릭 테스트 엔드포인트 허용
				.requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/stores").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/stores/{store_id:[0-9]+}").permitAll()
				.requestMatchers("/s/**").permitAll()
				.requestMatchers("/error").permitAll()
				// 인증 필요 엔드포인트
				.requestMatchers("/api/v1/permissions/**").authenticated()
				.requestMatchers("/api/v1/reviews/**").authenticated()
				.requestMatchers("/api/v1/categories", "/api/v1/categories/**").authenticated()
				// 기본 정책 - 나머지 모든 요청은 인증 필요
				.anyRequest().authenticated()
			)
			// JWT 인증 필터 추가
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, jwtBlacklistService),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * 비밀번호 암호화 설정
	 * - BCrypt 해시 알고리즘 사용
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
