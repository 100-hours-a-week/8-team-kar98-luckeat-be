package com.luckeat.luckeatbackend.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

	@Schema(description = "사용자 ID", example = "123")
	private Long userId;
	
	@Schema(description = "사용자 이메일", example = "user@example.com")
	private String email;
	
	@Schema(description = "사용자 닉네임", example = "홍길동")
	private String nickname;
	
	@Schema(description = "사용자 역할", example = "BUYER")
	private String role;
	
	@Schema(description = "인증 타입", example = "Bearer")
	private String grantType; // Bearer
	
	@Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String accessToken;
	
	@Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String refreshToken;
}