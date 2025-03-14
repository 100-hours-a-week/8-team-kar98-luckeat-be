package com.luckeat.luckeatbackend.common.exception;

public class BadRequestException extends CustomException {
	public BadRequestException(String message) {
		super(ErrorCode.BAD_REQUEST, message);
	}

	public BadRequestException() {
		super(ErrorCode.BAD_REQUEST);
	}
}