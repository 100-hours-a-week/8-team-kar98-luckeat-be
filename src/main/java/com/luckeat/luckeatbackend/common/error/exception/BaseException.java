package com.luckeat.luckeatbackend.common.error.exception;

import com.luckeat.luckeatbackend.common.error.ErrorCode;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

	private final ErrorCode errorCode;

	public BaseException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public BaseException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
}
