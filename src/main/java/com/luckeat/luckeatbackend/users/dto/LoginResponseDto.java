package com.luckeat.luckeatbackend.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
	private Long userId;
	private String email;
	private String nickname;
	private String role;
	private String grantType; // Bearer
	private String accessToken;
	private String refreshToken;
}