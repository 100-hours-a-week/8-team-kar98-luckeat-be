package com.luckeat.luckeatbackend.common.exception.category;

import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

public class CategoryNameDuplicateException extends ConflictException {
	public CategoryNameDuplicateException() {
		super("카테고리 이름 중복");
	}

	public CategoryNameDuplicateException(String message) {
		super(message);
	}
}