package com.luckeat.luckeatbackend.common.exception.permission;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

public class PermissionAlreadyExistsException extends ConflictException {
    public PermissionAlreadyExistsException() {
        super(ErrorCode.PERMISSION_DUPLICATE, ErrorCode.PERMISSION_DUPLICATE.getMessage());
    }
    
    public PermissionAlreadyExistsException(String message) {
        super(ErrorCode.PERMISSION_DUPLICATE, message);
    }
} 