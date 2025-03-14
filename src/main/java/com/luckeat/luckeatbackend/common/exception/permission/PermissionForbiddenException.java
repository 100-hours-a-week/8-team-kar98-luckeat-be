package com.luckeat.luckeatbackend.common.exception.permission;

import com.luckeat.luckeatbackend.common.exception.base.ForbiddenException;

public class PermissionForbiddenException extends ForbiddenException {
	public PermissionForbiddenException() {
		super("리뷰 작성 권한이 없음");
	}

	public PermissionForbiddenException(String message) {
		super(message);
	}
}