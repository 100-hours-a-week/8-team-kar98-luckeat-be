package com.luckeat.luckeatbackend.common.exception.permission;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class PermissionInvalidRoleException extends BadRequestException {
	public PermissionInvalidRoleException() {
		super(ErrorCode.PERMISSION_INVALID_ROLE, ErrorCode.PERMISSION_INVALID_ROLE.getMessage());
	}

	public PermissionInvalidRoleException(String message) {
		super(ErrorCode.PERMISSION_INVALID_ROLE, message);
	}
}