package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ReviewInvalidRatingException extends BadRequestException {
    public ReviewInvalidRatingException() {
        super("올바른 평점 형식이 아닙니다");
    }
    
    public ReviewInvalidRatingException(String message) {
        super(message);
    }
} 