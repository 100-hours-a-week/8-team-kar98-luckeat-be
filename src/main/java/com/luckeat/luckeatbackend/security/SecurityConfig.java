package com.luckeat.luckeatbackend.security;

import java.util.Arrays;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.luckeat.luckeatbackend.security.jwt.JwtAuthenticationFilter;
import com.luckeat.luckeatbackend.security.jwt.JwtTokenProvider;
import com.luckeat.luckeatbackend.users.service.JwtBlacklistService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtBlacklistService jwtBlacklistService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
						.authorizeHttpRequests(authorize -> authorize.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers("/api/auth/**").permitAll().requestMatchers("/api/users/signup").permitAll()
						.requestMatchers("/api/users/login").permitAll().requestMatchers("/api/v1/users/login")
						.permitAll().requestMatchers("/api/v1/users/register").permitAll()
						.requestMatchers("/api/v1/users/register").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/stores/**").permitAll()
						.requestMatchers("/api/v1/permissions/**").authenticated()
						.requestMatchers("/api/v1/reviews/**").authenticated()
						.requestMatchers("/s/**").authenticated()
						.requestMatchers("/api/v1/categories", "/api/v1/categories/**").authenticated().anyRequest()
						.authenticated())
				.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, jwtBlacklistService),
						UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(false);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
