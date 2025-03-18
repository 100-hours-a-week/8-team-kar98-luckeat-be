package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class StoreInvalidAddressException extends BadRequestException {
	public StoreInvalidAddressException() {
		super(ErrorCode.STORE_INVALID_ADDRESS, ErrorCode.STORE_INVALID_ADDRESS.getMessage());
	}

	public StoreInvalidAddressException(String message) {
		super(ErrorCode.STORE_INVALID_ADDRESS, message);
	}
}