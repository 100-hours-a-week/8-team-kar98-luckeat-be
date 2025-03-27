package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class UserInvalidNicknameException extends BadRequestException {
	public UserInvalidNicknameException() {
		super(ErrorCode.USER_INVALID_NICKNAME, ErrorCode.USER_INVALID_NICKNAME.getMessage());
	}

	public UserInvalidNicknameException(String message) {
		super(ErrorCode.USER_INVALID_NICKNAME, message);
	}
}