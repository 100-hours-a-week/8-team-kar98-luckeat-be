package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
    
    public BadRequestException() {
        super(ErrorCode.BAD_REQUEST);
    }
} 