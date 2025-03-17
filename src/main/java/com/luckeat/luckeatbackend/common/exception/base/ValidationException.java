package com.luckeat.luckeatbackend.common.exception.base;

public class ValidationException extends BadRequestException {
	public ValidationException() {
		super("유효성 검사 실패");
	}

	public ValidationException(String message) {
		super(message);
	}
}