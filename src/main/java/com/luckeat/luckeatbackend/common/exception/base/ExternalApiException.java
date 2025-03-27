package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

import lombok.Getter;
@Getter
public class ExternalApiException extends InternalServerErrorException {
	public ExternalApiException() {
		super(ErrorCode.EXTERNAL_API_ERROR, ErrorCode.EXTERNAL_API_ERROR.getMessage());
	}

	public ExternalApiException(String message) {
		super(ErrorCode.EXTERNAL_API_ERROR, message);
	}

}