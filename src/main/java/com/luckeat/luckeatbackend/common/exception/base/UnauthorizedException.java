package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
    
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
} 