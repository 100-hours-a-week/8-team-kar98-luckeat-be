package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ProductInvalidNameException extends BadRequestException {
	public ProductInvalidNameException() {
		super(ErrorCode.PRODUCT_INVALID_NAME, ErrorCode.PRODUCT_INVALID_NAME.getMessage());
	}

	public ProductInvalidNameException(String message) {
		super(ErrorCode.PRODUCT_INVALID_NAME, message);
	}
}