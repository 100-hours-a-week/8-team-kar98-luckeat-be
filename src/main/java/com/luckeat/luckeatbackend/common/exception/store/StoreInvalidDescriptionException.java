package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class StoreInvalidDescriptionException extends BadRequestException {
	public StoreInvalidDescriptionException() {
		super(ErrorCode.STORE_INVALID_DESCRIPTION, ErrorCode.STORE_INVALID_DESCRIPTION.getMessage());
	}

	public StoreInvalidDescriptionException(String message) {
		super(ErrorCode.STORE_INVALID_DESCRIPTION, message);
	}
}