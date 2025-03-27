package com.luckeat.luckeatbackend.common.exception.category;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class CategoryInvalidNameException extends BadRequestException {
	public CategoryInvalidNameException() {
		super(ErrorCode.CATEGORY_INVALID_NAME, ErrorCode.CATEGORY_INVALID_NAME.getMessage());
	}

	public CategoryInvalidNameException(String message) {
		super(ErrorCode.CATEGORY_INVALID_NAME, message);
	}
}