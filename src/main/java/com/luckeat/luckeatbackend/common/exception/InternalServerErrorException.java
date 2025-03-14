package com.luckeat.luckeatbackend.common.exception;

public class InternalServerErrorException extends CustomException {
	public InternalServerErrorException(String message) {
		super(ErrorCode.INTERNAL_SERVER_ERROR, message);
	}

	public InternalServerErrorException() {
		super(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}