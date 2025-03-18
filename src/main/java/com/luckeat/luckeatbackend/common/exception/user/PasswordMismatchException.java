package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class PasswordMismatchException extends BadRequestException {
    public PasswordMismatchException() {
        super(ErrorCode.WRONG_PASSWORD, ErrorCode.WRONG_PASSWORD.getMessage());
    }
} 