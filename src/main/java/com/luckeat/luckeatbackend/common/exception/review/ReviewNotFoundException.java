package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.base.NotFoundException;

public class ReviewNotFoundException extends NotFoundException {
	public ReviewNotFoundException() {
		super("리뷰 정보 없음");
	}

	public ReviewNotFoundException(String message) {
		super(message);
	}
}