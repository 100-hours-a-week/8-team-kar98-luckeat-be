package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.base.ConflictException;

public class NicknameDuplicateException extends ConflictException {
	public NicknameDuplicateException() {
		super("닉네임 중복");
	}

	public NicknameDuplicateException(String message) {
		super(message);
	}
}