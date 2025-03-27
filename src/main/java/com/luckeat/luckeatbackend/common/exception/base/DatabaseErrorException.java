package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class DatabaseErrorException extends CustomException {
	public DatabaseErrorException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public DatabaseErrorException() {
		super(ErrorCode.DATABASE_ERROR, ErrorCode.DATABASE_ERROR.getMessage());
	}
}