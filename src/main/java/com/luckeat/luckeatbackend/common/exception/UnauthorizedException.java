package com.luckeat.luckeatbackend.common.exception;

public class UnauthorizedException extends CustomException {
	public UnauthorizedException(String message) {
		super(ErrorCode.UNAUTHORIZED, message);
	}

	public UnauthorizedException() {
		super(ErrorCode.UNAUTHORIZED);
	}
}