package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class ValidationException extends BadRequestException {
	public ValidationException() {
		super(ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getMessage());
	}

	public ValidationException(String message) {
		super(ErrorCode.VALIDATION_ERROR, message);
	}
}