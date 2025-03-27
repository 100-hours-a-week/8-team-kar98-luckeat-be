package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
	public ProductNotFoundException() {
		super(ErrorCode.PRODUCT_NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}

	public ProductNotFoundException(String message) {
		super(ErrorCode.PRODUCT_NOT_FOUND, message);
	}
}