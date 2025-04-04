package com.luckeat.luckeatbackend.common.exception.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final ErrorCode errorCode;
	private static final Logger apiLogger = LoggerFactory.getLogger("API_LOGGER");

	public CustomException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		apiLogger.error("[{}] {}: {}", errorCode.name(), errorCode.getMessage(), message);
	}

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		apiLogger.error("[{}] {}", errorCode.name(), errorCode.getMessage());
	}
}