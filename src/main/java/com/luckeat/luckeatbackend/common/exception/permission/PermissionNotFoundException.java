package com.luckeat.luckeatbackend.common.exception.permission;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.NotFoundException;

public class PermissionNotFoundException extends NotFoundException {
	public PermissionNotFoundException() {
		super(ErrorCode.PERMISSION_NOT_FOUND, ErrorCode.PERMISSION_NOT_FOUND.getMessage());
	}

	public PermissionNotFoundException(String message) {
		super(ErrorCode.PERMISSION_NOT_FOUND, message);
	}
}