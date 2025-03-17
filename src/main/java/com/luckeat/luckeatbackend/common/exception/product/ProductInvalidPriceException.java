package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ProductInvalidPriceException extends BadRequestException {
	public ProductInvalidPriceException() {
		super("올바른 상품 가격 형식이 아닙니다");
	}

	public ProductInvalidPriceException(String message) {
		super(message);
	}
}