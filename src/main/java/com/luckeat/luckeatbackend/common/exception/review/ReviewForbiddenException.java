package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ForbiddenException;

public class ReviewForbiddenException extends ForbiddenException {
	public ReviewForbiddenException() {
		super(ErrorCode.REVIEW_FORBIDDEN, ErrorCode.REVIEW_FORBIDDEN.getMessage());
	}

	public ReviewForbiddenException(String message) {
		super(ErrorCode.REVIEW_FORBIDDEN, message);
	}
}