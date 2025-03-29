package com.luckeat.luckeatbackend.common.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
	private final String message;
	private final int status;
	private final String code;

	public ErrorResponse(ErrorCode errorCode) {
		this.message = errorCode.getMessage();
		this.status = errorCode.getStatus();
		this.code = errorCode.name();
	}
	
	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.code = null;
	}

	public ErrorResponse(ErrorCode errorCode, String message) {
		this.status = errorCode.getStatus();
		this.code = errorCode.name();
		this.message = message;
	}
}
