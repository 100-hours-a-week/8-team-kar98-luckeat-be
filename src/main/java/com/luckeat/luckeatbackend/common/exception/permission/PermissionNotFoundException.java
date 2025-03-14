package com.luckeat.luckeatbackend.common.exception.permission;

import com.luckeat.luckeatbackend.common.exception.NotFoundException;

public class PermissionNotFoundException extends NotFoundException {
	public PermissionNotFoundException() {
		super("리뷰 작성 권한 정보를 찾을 수 없음");
	}

	public PermissionNotFoundException(String message) {
		super(message);
	}
}