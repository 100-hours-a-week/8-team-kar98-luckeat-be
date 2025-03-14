package com.luckeat.luckeatbackend.common.exception.permission;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class PermissionInvalidRoleException extends BadRequestException {
    public PermissionInvalidRoleException() {
        super("올바른 권한 역할이 아닙니다");
    }
    
    public PermissionInvalidRoleException(String message) {
        super(message);
    }
}