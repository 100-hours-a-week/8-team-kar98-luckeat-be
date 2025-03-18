package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.NotFoundException;

public class UserNotFoundException extends NotFoundException {
	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage());
	}

	public UserNotFoundException(String message) {
		super(ErrorCode.USER_NOT_FOUND, message);
	}
}