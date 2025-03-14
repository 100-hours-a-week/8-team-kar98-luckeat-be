package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.NotFoundException;

public class StoreNotFoundException extends NotFoundException {
	public StoreNotFoundException() {
		super("가게 정보 없음");
	}

	public StoreNotFoundException(String message) {
		super(message);
	}
}