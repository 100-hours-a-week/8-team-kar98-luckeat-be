package com.luckeat.luckeatbackend.security.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.luckeat.luckeatbackend.users.service.JwtBlacklistService;
import org.springframework.context.annotation.Bean;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	private JwtBlacklistService jwtBlacklistService;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, JwtBlacklistService jwtBlacklistService) {
       this.jwtTokenProvider = jwtTokenProvider;
       this.jwtBlacklistService = jwtBlacklistService;
   }

      @Bean
   public JwtAuthenticationFilter jwtAuthenticationFilter() {
       return new JwtAuthenticationFilter(jwtTokenProvider, jwtBlacklistService);
   }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 요청에서 JWT 토큰 추출
		String token = resolveToken(request);

		// 토큰 유효성 검증 및 인증 처리
		if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
			// 블랙리스트 확인
			if (jwtBlacklistService.isTokenBlacklisted(token)) {
				log.debug("블랙리스트에 있는 JWT 토큰입니다, uri: {}", request.getRequestURI());
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "블랙리스트에 있는 토큰입니다.");
				return; // 블랙리스트에 있는 경우 인증을 중단
			}

			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("토큰 인증 성공, uri: {}", request.getRequestURI());
		} else {
			log.debug("유효한 JWT 토큰이 없습니다, uri: {}", request.getRequestURI());
		}

		filterChain.doFilter(request, response);
	}

	// Authorization 헤더에서 토큰 추출
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}