package com.luckeat.luckeatbackend.common.exception;

public class NotFoundException extends CustomException {
	public NotFoundException(String message) {
		super(ErrorCode.NOT_FOUND, message);
	}

	public NotFoundException() {
		super(ErrorCode.NOT_FOUND);
	}
}