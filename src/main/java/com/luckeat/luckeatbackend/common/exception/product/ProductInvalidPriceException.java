package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ProductInvalidPriceException extends BadRequestException {
	public ProductInvalidPriceException() {
		super(ErrorCode.PRODUCT_INVALID_PRICE, ErrorCode.PRODUCT_INVALID_PRICE.getMessage());
	}

	public ProductInvalidPriceException(String message) {
		super(ErrorCode.PRODUCT_INVALID_PRICE, message);
	}
}