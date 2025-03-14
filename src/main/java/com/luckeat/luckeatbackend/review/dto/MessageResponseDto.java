package com.luckeat.luckeatbackend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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