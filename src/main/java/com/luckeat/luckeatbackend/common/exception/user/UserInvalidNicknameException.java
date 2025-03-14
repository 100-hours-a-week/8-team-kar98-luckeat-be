package com.luckeat.luckeatbackend.common.exception.user;

import com.luckeat.luckeatbackend.common.exception.base.BadRequestException;

public class UserInvalidNicknameException extends BadRequestException {
    public UserInvalidNicknameException() {
        super("올바른 닉네임 형식이 아닙니다");
    }
    
    public UserInvalidNicknameException(String message) {
        super(message);
    }
} 