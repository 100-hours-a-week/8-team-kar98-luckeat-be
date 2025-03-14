package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ProductInvalidImageException extends BadRequestException {
    public ProductInvalidImageException() {
        super("올바른 상품 이미지 형식이 아닙니다");
    }
    
    public ProductInvalidImageException(String message) {
        super(message);
    }
} 