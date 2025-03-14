package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ReviewInvalidContentException extends BadRequestException {
    public ReviewInvalidContentException() {
        super("올바른 리뷰 내용 형식이 아닙니다");
    }
    
    public ReviewInvalidContentException(String message) {
        super(message);
    }
} 