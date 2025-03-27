package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class UserInvalidPasswordException extends BadRequestException {
	public UserInvalidPasswordException() {
		super(ErrorCode.USER_INVALID_PASSWORD, ErrorCode.USER_INVALID_PASSWORD.getMessage());
	}

	public UserInvalidPasswordException(String message) {
		super(ErrorCode.USER_INVALID_PASSWORD, message);
	}
}