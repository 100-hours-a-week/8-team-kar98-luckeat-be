package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.UnauthorizedException;

public class ProductUnauthenticatedException extends UnauthorizedException {
    public ProductUnauthenticatedException() {
        super(ErrorCode.UNAUTHENTICATED, ErrorCode.UNAUTHENTICATED.getMessage());
    }
    
    public ProductUnauthenticatedException(String message) {
        super(ErrorCode.UNAUTHENTICATED, message);
    }
} 