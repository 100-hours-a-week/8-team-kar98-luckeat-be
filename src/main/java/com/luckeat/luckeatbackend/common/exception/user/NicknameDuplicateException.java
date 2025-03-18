package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

public class NicknameDuplicateException extends ConflictException {
	public NicknameDuplicateException() {
		super(ErrorCode.NICKNAME_DUPLICATE, ErrorCode.NICKNAME_DUPLICATE.getMessage());
	}

	public NicknameDuplicateException(String message) {
		super(ErrorCode.NICKNAME_DUPLICATE, message);
	}
}