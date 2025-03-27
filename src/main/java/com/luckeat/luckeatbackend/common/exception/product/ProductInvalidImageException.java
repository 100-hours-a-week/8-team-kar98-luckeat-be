package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ProductInvalidImageException extends BadRequestException {
	public ProductInvalidImageException() {
		super(ErrorCode.PRODUCT_INVALID_IMAGE, ErrorCode.PRODUCT_INVALID_IMAGE.getMessage());
	}

	public ProductInvalidImageException(String message) {
		super(ErrorCode.PRODUCT_INVALID_IMAGE, message);
	}
}