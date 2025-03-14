package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.ForbiddenException;

public class ReviewForbiddenException extends ForbiddenException {
	public ReviewForbiddenException() {
		super("리뷰 접근 권한 없음");
	}

	public ReviewForbiddenException(String message) {
		super(message);
	}
}