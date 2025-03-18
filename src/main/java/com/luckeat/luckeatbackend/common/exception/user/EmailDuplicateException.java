package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

public class EmailDuplicateException extends ConflictException {
	public EmailDuplicateException() {
		super(ErrorCode.EMAIL_DUPLICATE, ErrorCode.EMAIL_DUPLICATE.getMessage());
	}

	public EmailDuplicateException(String message) {
		super(ErrorCode.EMAIL_DUPLICATE, message);
	}
}