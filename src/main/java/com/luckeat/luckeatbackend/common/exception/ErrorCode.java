package com.luckeat.luckeatbackend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	// 공통 에러
	BAD_REQUEST(400, "잘못된 요청"), UNAUTHORIZED(401, "인증 필요"), FORBIDDEN(403, "권한 없음"), NOT_FOUND(404,
			"리소스를 찾을 수 없음"), CONFLICT(409, "리소스 충돌"), INTERNAL_SERVER_ERROR(500, "서버 오류"), DATABASE_ERROR(500,
					"데이터베이스 오류"), FILE_UPLOAD_ERROR(500,
							"파일 업로드 오류"), VALIDATION_ERROR(400, "유효성 검사 실패"), EXTERNAL_API_ERROR(500, "외부 API 호출 실패"),
	METHOD_NOT_ALLOWED(405, "지원하지 않는 HTTP 메소드입니다"),
	// 사용자 관련 에러
	USER_NOT_FOUND(404, "사용자 정보 없음"), EMAIL_DUPLICATE(409, "이메일 중복"), NICKNAME_DUPLICATE(409,
			"닉네임 중복"), USER_INVALID_EMAIL(400, "올바른 이메일 형식이 아닙니다"), USER_INVALID_NICKNAME(400,
					"올바른 닉네임 형식이 아닙니다"), USER_INVALID_PASSWORD(400, "비밀번호가 일치하지 않습니다"),
	// 추가된 사용자 관련 에러
	INVALID_ROLE(400, "역할은 ADMIN, BUYER, SELLER 중 하나여야 합니다"),
	UNAUTHENTICATED(401, "인증되지 않은 사용자입니다"),
	WRONG_PASSWORD(400, "비밀번호가 일치하지 않습니다"),
					
	// 가게 관련 에러
	STORE_NOT_FOUND(404, "가게 정보 없음"), STORE_FORBIDDEN(403, "가게 접근 권한 없음"), STORE_INVALID_ADDRESS(400,
			"올바른 가게 주소 형식이 아닙니다"), STORE_INVALID_PHONE_NUMBER(400,
					"올바른 가게 전화번호 형식이 아닙니다"), STORE_INVALID_BUSINESS_HOURS(400,
							"올바른 가게 영업시간 형식이 아닙니다"), STORE_INVALID_DESCRIPTION(400, "올바른 가게 설명 형식이 아닙니다"),

	// 상품 관련 에러
	PRODUCT_NOT_FOUND(404, "상품 정보 없음"), PRODUCT_NAME_DUPLICATE(409, "상품 이름 중복"), PRODUCT_FORBIDDEN(403,
			"상품 접근 권한 없음"), PRODUCT_INVALID_NAME(400, "올바른 상품 이름 형식이 아닙니다"), PRODUCT_INVALID_PRICE(400,
					"올바른 상품 가격 형식이 아닙니다"), PRODUCT_INVALID_IMAGE(400, "올바른 상품 이미지 형식이 아닙니다"),

	// 리뷰 관련 에러
	REVIEW_NOT_FOUND(404, "리뷰 정보 없음"), REVIEW_FORBIDDEN(403, "리뷰 접근 권한 없음"), REVIEW_INVALID_CONTENT(400,
			"올바른 리뷰 내용 형식이 아닙니다"), REVIEW_INVALID_RATING(400,
					"올바른 평점 형식이 아닙니다"), REVIEW_INVALID_IMAGE(400, "올바른 이미지 형식이 아닙니다"),

	// 권한 관련 에러
	PERMISSION_NOT_FOUND(404, "리뷰 작성 권한 정보를 찾을 수 없음"), PERMISSION_FORBIDDEN(403,
			"리뷰 작성 권한이 없음"), PERMISSION_INVALID_ROLE(400, "올바른 권한 역할이 아닙니다"),

	// 카테고리 관련 에러
	CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없음"), CATEGORY_INVALID_NAME(400, "카테고리 이름 유효성 검사 실패"), CATEGORY_INVALID_IMAGE(
			400, "카테고리 이미지 유효성 검사 실패"), CATEGORY_NAME_DUPLICATE(409, "카테고리 이름 중복"),

	// 권한 관련 에러
	PERMISSION_DUPLICATE(409, "이미 권한이 존재합니다");

	private final int status;
	private final String message;
}