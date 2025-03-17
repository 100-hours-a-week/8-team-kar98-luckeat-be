package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class StoreInvalidPhoneNumberException extends BadRequestException {
	public StoreInvalidPhoneNumberException() {
		super("올바른 전화번호 형식이 아닙니다");
	}

	public StoreInvalidPhoneNumberException(String message) {
		super(message);
	}
}