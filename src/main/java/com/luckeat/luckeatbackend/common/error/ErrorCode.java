package com.luckeat.luckeatbackend.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 공통 에러
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다"), RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C002",
			"리소스를 찾을 수 없습니다"), INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 오류가 발생했습니다"),

	// 사용자 관련 에러
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다"), DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002",
			"이미 사용 중인 이메일입니다"),

	// 상점 관련 에러
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "상점을 찾을 수 없습니다"),

	// 리뷰 관련 에러
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "리뷰를 찾을 수 없습니다"), UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN,
			"R002", "리뷰에 접근할 권한이 없습니다");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
