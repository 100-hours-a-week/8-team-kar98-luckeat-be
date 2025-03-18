package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class ForbiddenException extends CustomException {
	public ForbiddenException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public ForbiddenException() {
		super(ErrorCode.FORBIDDEN, ErrorCode.FORBIDDEN.getMessage());
	}
}