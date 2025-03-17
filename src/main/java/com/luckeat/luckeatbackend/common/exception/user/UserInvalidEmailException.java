package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class UserInvalidEmailException extends BadRequestException {
	public UserInvalidEmailException() {
		super("올바른 이메일 형식이 아닙니다");
	}

	public UserInvalidEmailException(String message) {
		super(message);
	}
}