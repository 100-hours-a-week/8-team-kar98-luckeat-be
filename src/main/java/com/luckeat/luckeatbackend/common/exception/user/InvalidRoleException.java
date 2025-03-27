package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class InvalidRoleException extends BadRequestException {
    public InvalidRoleException() {
        super(ErrorCode.INVALID_ROLE, ErrorCode.INVALID_ROLE.getMessage());
    }
} 