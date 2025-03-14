package com.luckeat.luckeatbackend.common.exception.category;

import com.luckeat.luckeatbackend.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
	public CategoryNotFoundException() {
		super("카테고리를 찾을 수 없습니다");
	}

	public CategoryNotFoundException(String message) {
		super(message);
	}
}