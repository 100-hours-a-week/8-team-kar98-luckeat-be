package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ReviewInvalidContentException extends BadRequestException {
	public ReviewInvalidContentException() {
		super(ErrorCode.REVIEW_INVALID_CONTENT, ErrorCode.REVIEW_INVALID_CONTENT.getMessage());
	}

	public ReviewInvalidContentException(String message) {
		super(ErrorCode.REVIEW_INVALID_CONTENT, message);
	}
}