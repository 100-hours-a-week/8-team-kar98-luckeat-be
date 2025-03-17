package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class StoreInvalidBusinessHoursException extends BadRequestException {
	public StoreInvalidBusinessHoursException() {
		super("올바른 영업시간 형식이 아닙니다");
	}

	public StoreInvalidBusinessHoursException(String message) {
		super(message);
	}
}