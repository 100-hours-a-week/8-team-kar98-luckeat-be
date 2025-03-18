package com.luckeat.luckeatbackend.common.exception.base;

import com.luckeat.luckeatbackend.common.exception.ErrorCode;

public class FileUploadException extends BadRequestException {
	public FileUploadException(String message) {
		super(ErrorCode.FILE_UPLOAD_ERROR, message);
	}

	public FileUploadException() {
		super(ErrorCode.FILE_UPLOAD_ERROR, ErrorCode.FILE_UPLOAD_ERROR.getMessage());
	}
}