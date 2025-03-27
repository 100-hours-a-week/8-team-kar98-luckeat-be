package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class ConflictException extends CustomException {
	public ConflictException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public ConflictException() {
		super(ErrorCode.CONFLICT, ErrorCode.CONFLICT.getMessage());
	}
}