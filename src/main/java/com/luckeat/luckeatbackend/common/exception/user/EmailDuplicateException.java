package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.ConflictException;

public class EmailDuplicateException extends ConflictException {
	public EmailDuplicateException() {
		super("이메일 중복");
	}

	public EmailDuplicateException(String message) {
		super(message);
	}
}