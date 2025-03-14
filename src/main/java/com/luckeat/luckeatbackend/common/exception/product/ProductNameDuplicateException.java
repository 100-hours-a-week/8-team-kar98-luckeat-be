package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

public class ProductNameDuplicateException extends ConflictException {
	public ProductNameDuplicateException() {
		super("상품 이름 중복");
	}

	public ProductNameDuplicateException(String message) {
		super(message);
	}
}