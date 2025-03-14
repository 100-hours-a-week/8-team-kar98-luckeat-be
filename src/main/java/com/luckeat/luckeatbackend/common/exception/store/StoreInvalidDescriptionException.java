package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class StoreInvalidDescriptionException extends BadRequestException {
    public StoreInvalidDescriptionException() {
        super("올바른 가게 설명 형식이 아닙니다");
    }
    
    public StoreInvalidDescriptionException(String message) {
        super(message);
    }
} 