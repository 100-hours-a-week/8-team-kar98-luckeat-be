package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class BadRequestException extends CustomException {
	public BadRequestException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public BadRequestException() {
		super(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage());
	}
}