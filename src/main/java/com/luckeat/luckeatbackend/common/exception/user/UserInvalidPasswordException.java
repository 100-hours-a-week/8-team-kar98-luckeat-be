package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class UserInvalidPasswordException extends BadRequestException {
	public UserInvalidPasswordException() {
		super("비밀번호가 일치하지 않습니다");
	}

	public UserInvalidPasswordException(String message) {
		super(message);
	}
}