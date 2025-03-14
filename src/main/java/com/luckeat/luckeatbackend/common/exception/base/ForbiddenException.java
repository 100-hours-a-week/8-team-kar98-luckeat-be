package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class ForbiddenException extends CustomException {
	public ForbiddenException(String message) {
		super(ErrorCode.FORBIDDEN, message);
	}

	public ForbiddenException() {
		super(ErrorCode.FORBIDDEN);
	}
}