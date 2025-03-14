package com.luckeat.luckeatbackend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	// 공통 에러
	BAD_REQUEST(400, "잘못된 요청"), UNAUTHORIZED(401, "인증 필요"), FORBIDDEN(403, "권한 없음"), NOT_FOUND(404,
			"리소스를 찾을 수 없음"), CONFLICT(409,
					"리소스 충돌"), INTERNAL_SERVER_ERROR(500, "서버 오류"), DATABASE_ERROR(500, "데이터베이스 오류"),

	// 사용자 관련 에러
	USER_NOT_FOUND(404, "사용자 정보 없음"), EMAIL_DUPLICATE(409, "이메일 중복"), NICKNAME_DUPLICATE(409, "닉네임 중복"),

	// 가게 관련 에러
	STORE_NOT_FOUND(404, "가게 정보 없음"), STORE_FORBIDDEN(403, "가게 접근 권한 없음"),

	// 상품 관련 에러
	PRODUCT_NOT_FOUND(404, "상품 정보 없음"), PRODUCT_NAME_DUPLICATE(409, "상품 이름 중복"), PRODUCT_FORBIDDEN(403, "상품 접근 권한 없음"),

	// 리뷰 관련 에러
	REVIEW_NOT_FOUND(404, "리뷰 정보 없음"), REVIEW_FORBIDDEN(403, "리뷰 접근 권한 없음"),

	// 권한 관련 에러
	PERMISSION_NOT_FOUND(404, "리뷰 작성 권한 정보를 찾을 수 없음"), PERMISSION_FORBIDDEN(403, "리뷰 작성 권한이 없음"),

	// 카테고리 관련 에러
	CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없음");

	private final int status;
	private final String message;
}