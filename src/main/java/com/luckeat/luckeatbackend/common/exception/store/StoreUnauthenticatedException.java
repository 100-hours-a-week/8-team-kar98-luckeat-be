package com.luckeat.luckeatbackend.common.exception.store;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.CustomException;

/**
 * 인증되지 않은 사용자가 스토어 기능에 접근 시 발생하는 예외
 */
public class StoreUnauthenticatedException extends CustomException {
    public StoreUnauthenticatedException() {
        super(ErrorCode.UNAUTHORIZED, "스토어 기능 접근 시 인증이 필요합니다.");
    }

    public StoreUnauthenticatedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
} 