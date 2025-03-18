package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ReviewInvalidImageException extends BadRequestException {
	public ReviewInvalidImageException() {
		super(ErrorCode.REVIEW_INVALID_IMAGE, ErrorCode.REVIEW_INVALID_IMAGE.getMessage());
	}

	public ReviewInvalidImageException(String message) {
		super(ErrorCode.REVIEW_INVALID_IMAGE, message);
	}
}