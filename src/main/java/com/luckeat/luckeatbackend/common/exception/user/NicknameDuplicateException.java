package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;
import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 중복 에러", example = "{\"message\": \"닉네임 중복\", \"status\": 409}")
public class NicknameDuplicateException extends ConflictException {
	public NicknameDuplicateException() {
		super(ErrorCode.NICKNAME_DUPLICATE, ErrorCode.NICKNAME_DUPLICATE.getMessage());
	}

	public NicknameDuplicateException(String message) {
		super(ErrorCode.NICKNAME_DUPLICATE, message);
	}
}