package com.luckeat.luckeatbackend.common.exception.category;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class CategoryInvalidImageException extends BadRequestException {
	public CategoryInvalidImageException() {
		super(ErrorCode.CATEGORY_INVALID_IMAGE, ErrorCode.CATEGORY_INVALID_IMAGE.getMessage());
	}

	public CategoryInvalidImageException(String message) {
		super(ErrorCode.CATEGORY_INVALID_IMAGE, message);
	}
}