package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class UnauthorizedException extends CustomException {
	public UnauthorizedException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public UnauthorizedException() {
		super(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
	}
}