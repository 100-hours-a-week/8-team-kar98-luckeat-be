package com.luckeat.luckeatbackend.common.exception.permission;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ForbiddenException;

public class PermissionForbiddenException extends ForbiddenException {
	public PermissionForbiddenException() {
		super(ErrorCode.PERMISSION_FORBIDDEN, ErrorCode.PERMISSION_FORBIDDEN.getMessage());
	}

	public PermissionForbiddenException(String message) {
		super(ErrorCode.PERMISSION_FORBIDDEN, message);
	}
}