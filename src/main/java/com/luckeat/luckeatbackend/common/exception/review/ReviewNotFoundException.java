package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.CustomException;

public class ReviewNotFoundException extends CustomException {
	public ReviewNotFoundException() {
		super(ErrorCode.REVIEW_NOT_FOUND, ErrorCode.REVIEW_NOT_FOUND.getMessage());
	}

	public ReviewNotFoundException(String message) {
		super(ErrorCode.REVIEW_NOT_FOUND, message);
	}
}