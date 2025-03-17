package com.luckeat.luckeatbackend.common.exception.base;

public class ExternalApiException extends InternalServerErrorException {
	public ExternalApiException() {
		super("외부 API 호출 실패");
	}

	public ExternalApiException(String message) {
		super(message);
	}
}