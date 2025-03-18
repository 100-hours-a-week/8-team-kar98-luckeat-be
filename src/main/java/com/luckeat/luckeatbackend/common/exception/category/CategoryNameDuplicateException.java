package com.luckeat.luckeatbackend.common.exception.category;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ConflictException;
public class CategoryNameDuplicateException extends ConflictException {
	public CategoryNameDuplicateException() {
		super(ErrorCode.CATEGORY_NAME_DUPLICATE, ErrorCode.CATEGORY_NAME_DUPLICATE.getMessage());
	}

	public CategoryNameDuplicateException(String message) {
		super(ErrorCode.CATEGORY_NAME_DUPLICATE, message);
	}
}