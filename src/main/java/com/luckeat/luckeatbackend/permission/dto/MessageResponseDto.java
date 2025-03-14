package com.luckeat.luckeatbackend.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 기본 메시지 응답을 위한 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {

	private String message;

	public static MessageResponseDto success(String message) {
		return MessageResponseDto.builder().message(message).build();
	}

	public static MessageResponseDto error(String message) {
		return MessageResponseDto.builder().message(message).build();
	}
}