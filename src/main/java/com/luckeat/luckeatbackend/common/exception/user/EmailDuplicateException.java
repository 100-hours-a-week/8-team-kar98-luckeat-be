package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 중복 에러", example = "{\"message\": \"이메일 중복\", \"status\": 409}")
public class EmailDuplicateException extends ConflictException {
	public EmailDuplicateException() {
		super(ErrorCode.EMAIL_DUPLICATE, ErrorCode.EMAIL_DUPLICATE.getMessage());
	}

	public EmailDuplicateException(String message) {
		super(ErrorCode.EMAIL_DUPLICATE, message);
	}
}