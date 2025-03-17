package com.luckeat.luckeatbackend.common.exception.base;

public class FileUploadException extends BadRequestException {
	public FileUploadException() {
		super("파일 업로드 실패");
	}

	public FileUploadException(String message) {
		super(message);
	}
}