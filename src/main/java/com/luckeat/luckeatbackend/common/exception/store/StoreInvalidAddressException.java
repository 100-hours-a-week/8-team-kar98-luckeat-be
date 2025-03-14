package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class StoreInvalidAddressException extends BadRequestException {
    public StoreInvalidAddressException() {
        super("올바른 가게 주소 형식이 아닙니다");
    }
    
    public StoreInvalidAddressException(String message) {
        super(message);
    }
} 