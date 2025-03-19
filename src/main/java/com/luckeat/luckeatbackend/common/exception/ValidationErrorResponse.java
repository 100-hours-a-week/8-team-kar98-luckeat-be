package com.luckeat.luckeatbackend.common.exception;

import java.util.Map;

import lombok.Getter;

@Getter
public class ValidationErrorResponse {
	private final String message;
	private final int status;
	private final Map<String, String> errors;

	public ValidationErrorResponse(ErrorCode errorCode, Map<String, String> errors) {
		this.message = errorCode.getMessage();
		this.status = errorCode.getStatus();
		this.errors = errors;
	}
} 
