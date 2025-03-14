package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class DatabaseErrorException extends CustomException {
	public DatabaseErrorException(String message) {
		super(ErrorCode.DATABASE_ERROR, message);
	}

	public DatabaseErrorException() {
		super(ErrorCode.DATABASE_ERROR);
	}
}