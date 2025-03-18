package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class NotFoundException extends CustomException {
	public NotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public NotFoundException() {
		super(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
	}
}