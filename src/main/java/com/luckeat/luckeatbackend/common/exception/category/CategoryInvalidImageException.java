package com.luckeat.luckeatbackend.common.exception.category;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class CategoryInvalidImageException extends BadRequestException {
	public CategoryInvalidImageException() {
		super("올바른 카테고리 이미지 형식이 아닙니다");
	}

	public CategoryInvalidImageException(String message) {
		super(message);
	}
}