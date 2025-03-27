package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class InternalServerErrorException extends CustomException {
	public InternalServerErrorException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public InternalServerErrorException() {
		super(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
	}

}