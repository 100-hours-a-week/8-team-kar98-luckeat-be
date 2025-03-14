package com.luckeat.luckeatbackend.common.exception.product;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class ProductInvalidNameException extends BadRequestException {
    public ProductInvalidNameException() {
        super("올바른 상품 이름 형식이 아닙니다");
    }

    public ProductInvalidNameException(String message) {
        super(message);
    }
}