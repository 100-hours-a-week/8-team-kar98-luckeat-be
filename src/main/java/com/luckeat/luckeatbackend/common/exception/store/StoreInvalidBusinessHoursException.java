package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class StoreInvalidBusinessHoursException extends BadRequestException {
	public StoreInvalidBusinessHoursException() {
		super(ErrorCode.STORE_INVALID_BUSINESS_HOURS, ErrorCode.STORE_INVALID_BUSINESS_HOURS.getMessage());
	}

	public StoreInvalidBusinessHoursException(String message) {
		super(ErrorCode.STORE_INVALID_BUSINESS_HOURS, message);
	}
}