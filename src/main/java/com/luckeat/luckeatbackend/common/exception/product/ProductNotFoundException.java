package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.base.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
	public ProductNotFoundException() {
		super("상품 정보 없음");
	}

	public ProductNotFoundException(String message) {
		super(message);
	}
}