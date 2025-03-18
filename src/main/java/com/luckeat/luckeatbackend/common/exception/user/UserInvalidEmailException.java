package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class UserInvalidEmailException extends BadRequestException {
	public UserInvalidEmailException() {
		super(ErrorCode.USER_INVALID_EMAIL, ErrorCode.USER_INVALID_EMAIL.getMessage());
	}

	public UserInvalidEmailException(String message) {
		super(ErrorCode.USER_INVALID_EMAIL, message);
	}
}