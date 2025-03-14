package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.base.ForbiddenException;

public class ProductForbiddenException extends ForbiddenException {
	public ProductForbiddenException() {
		super("상품 접근 권한 없음");
	}

	public ProductForbiddenException(String message) {
		super(message);
	}
}