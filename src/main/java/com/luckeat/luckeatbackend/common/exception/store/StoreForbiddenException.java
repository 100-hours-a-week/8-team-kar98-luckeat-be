package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ForbiddenException;

public class StoreForbiddenException extends ForbiddenException {
	public StoreForbiddenException() {
		super(ErrorCode.STORE_FORBIDDEN, ErrorCode.STORE_FORBIDDEN.getMessage());
	}

	public StoreForbiddenException(String message) {
		super(ErrorCode.STORE_FORBIDDEN, message);
	}
}