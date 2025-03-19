package com.luckeat.luckeatbackend.users.dto;

import java.time.format.DateTimeFormatter;

import com.luckeat.luckeatbackend.users.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {
	private String role;
	private String email;
	private String nickname;
	private String createdAt;
	private String updatedAt;

	public static UserInfoResponseDto fromEntity(User user) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

		return UserInfoResponseDto.builder().role(user.getRole().name()).email(user.getEmail())
				.nickname(user.getNickname())
				.createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : null)
				.updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().format(formatter) : null).build();
	}
}