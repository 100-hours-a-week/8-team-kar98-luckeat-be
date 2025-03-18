package com.luckeat.luckeatbackend.users.dto;

import com.luckeat.luckeatbackend.users.model.User;

import lombok.Getter;

@Getter
public class UserResponseDto {
	private Long id;
	private String email;
	private String nickname;
	// 필요한 다른 필드들

	public UserResponseDto(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		// 다른 필드 초기화
	}
}