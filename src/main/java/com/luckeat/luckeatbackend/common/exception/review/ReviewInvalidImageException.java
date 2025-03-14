package com.luckeat.luckeatbackend.common.exception.review;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ReviewInvalidImageException extends BadRequestException {
    public ReviewInvalidImageException() {
        super("올바른 이미지 형식이 아닙니다");
    }
    
    public ReviewInvalidImageException(String message) {
        super(message);
    }
} 