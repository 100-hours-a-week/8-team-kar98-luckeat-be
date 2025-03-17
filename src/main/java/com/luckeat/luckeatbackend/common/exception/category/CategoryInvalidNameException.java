package com.luckeat.luckeatbackend.common.exception.category;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class CategoryInvalidNameException extends BadRequestException {
	public CategoryInvalidNameException() {
		super("올바른 카테고리 이름 형식이 아닙니다");
	}

	public CategoryInvalidNameException(String message) {
		super(message);
	}
}