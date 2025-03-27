package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.NotFoundException;

public class StoreNotFoundException extends NotFoundException {
	public StoreNotFoundException() {
		super(ErrorCode.STORE_NOT_FOUND, ErrorCode.STORE_NOT_FOUND.getMessage());
	}

	public StoreNotFoundException(String message) {
		super(ErrorCode.STORE_NOT_FOUND, message);
	}
}