package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class StoreInvalidPhoneNumberException extends BadRequestException {
	public StoreInvalidPhoneNumberException() {
		super(ErrorCode.STORE_INVALID_PHONE_NUMBER, ErrorCode.STORE_INVALID_PHONE_NUMBER.getMessage());
	}

	public StoreInvalidPhoneNumberException(String message) {
		super(ErrorCode.STORE_INVALID_PHONE_NUMBER, message);
	}
}