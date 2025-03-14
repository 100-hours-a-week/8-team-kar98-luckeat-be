package com.luckeat.luckeatbackend.common.exception;

public class ConflictException extends CustomException {
	public ConflictException(String message) {
		super(ErrorCode.CONFLICT, message);
	}

	public ConflictException() {
		super(ErrorCode.CONFLICT);
	}
}