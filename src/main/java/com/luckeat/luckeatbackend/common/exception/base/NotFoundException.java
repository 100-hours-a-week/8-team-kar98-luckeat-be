package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class NotFoundException extends CustomException {
    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
    
    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
} 