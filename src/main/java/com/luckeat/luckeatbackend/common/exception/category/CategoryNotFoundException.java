package com.luckeat.luckeatbackend.common.exception.category;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
	public CategoryNotFoundException() {
		super(ErrorCode.CATEGORY_NOT_FOUND, ErrorCode.CATEGORY_NOT_FOUND.getMessage());
	}

	public CategoryNotFoundException(String message) {
		super(ErrorCode.CATEGORY_NOT_FOUND, message);
	}
}