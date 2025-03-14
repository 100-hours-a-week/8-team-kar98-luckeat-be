package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.base.NotFoundException;

public class UserNotFoundException extends NotFoundException {
	public UserNotFoundException() {
		super("사용자 정보 없음");
	}

	public UserNotFoundException(String message) {
		super(message);
	}
}