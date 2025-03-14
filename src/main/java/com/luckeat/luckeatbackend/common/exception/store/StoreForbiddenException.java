package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.ForbiddenException;

public class StoreForbiddenException extends ForbiddenException {
	public StoreForbiddenException() {
		super("가게 접근 권한 없음");
	}

	public StoreForbiddenException(String message) {
		super(message);
	}
}