package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ForbiddenException;

public class ProductForbiddenException extends ForbiddenException {
	public ProductForbiddenException() {
		super(ErrorCode.PRODUCT_FORBIDDEN, ErrorCode.PRODUCT_FORBIDDEN.getMessage());
	}

	public ProductForbiddenException(String message) {
		super(ErrorCode.PRODUCT_FORBIDDEN, message);
	}
}