package com.luckeat.luckeatbackend.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
	private final String message;

	public static ErrorResponse of(String message) {
		return ErrorResponse.builder().message(message).build();
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.builder().message(errorCode.getMessage()).build();
	}
}
