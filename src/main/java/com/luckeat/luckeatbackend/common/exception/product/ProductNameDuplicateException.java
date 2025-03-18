package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

public class ProductNameDuplicateException extends ConflictException {
	public ProductNameDuplicateException() {
		super(ErrorCode.PRODUCT_NAME_DUPLICATE, ErrorCode.PRODUCT_NAME_DUPLICATE.getMessage());
	}

	public ProductNameDuplicateException(String message) {
		super(ErrorCode.PRODUCT_NAME_DUPLICATE, message);
	}
}