package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ReviewInvalidRatingException extends BadRequestException {
	public ReviewInvalidRatingException() {
		super(ErrorCode.REVIEW_INVALID_RATING, ErrorCode.REVIEW_INVALID_RATING.getMessage());
	}

	public ReviewInvalidRatingException(String message) {
		super(ErrorCode.REVIEW_INVALID_RATING, message);
	}
}