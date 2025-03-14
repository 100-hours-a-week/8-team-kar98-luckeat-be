package com.luckeat.luckeatbackend.common.exception;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 사용자 정의 예외 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.error("CustomException: {}", e.getMessage());
		return ResponseEntity.status(e.getErrorCode().getStatus()).body(ErrorResponse.of(e.getErrorCode()));
	}

	// 인증 관련 예외 처리
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
		log.error("AuthenticationException: {}", e.getMessage());
		return ResponseEntity.status(401).body(ErrorResponse.of(ErrorCode.UNAUTHORIZED));
	}

	// 권한 관련 예외 처리
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
		log.error("AccessDeniedException: {}", e.getMessage());
		return ResponseEntity.status(403).body(ErrorResponse.of(ErrorCode.FORBIDDEN));
	}

	// 로그인 실패 예외 처리
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
		log.error("BadCredentialsException: {}", e.getMessage());
		return ResponseEntity.status(401).body(ErrorResponse.of("잘못된 인증 정보입니다."));
	}

	// 유효성 검사 예외 처리
	@ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
	public ResponseEntity<ErrorResponse> handleValidationException(Exception e) {
		log.error("ValidationException: {}", e.getMessage());
		return ResponseEntity.status(400).body(ErrorResponse.of(ErrorCode.BAD_REQUEST));
	}

	// 잘못된 요청 파라미터 타입 예외 처리
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException e) {
		log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());
		return ResponseEntity.status(400).body(ErrorResponse.of("요청 파라미터 타입이 올바르지 않습니다."));
	}

	// 데이터베이스 접근 예외 처리
	@ExceptionHandler({SQLException.class, DataAccessException.class})
	public ResponseEntity<ErrorResponse> handleDatabaseException(Exception e) {
		log.error("DatabaseException: {}", e.getMessage());
		return ResponseEntity.status(500).body(ErrorResponse.of(ErrorCode.DATABASE_ERROR));
	}

	// 그 외 모든 예외 처리
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("Exception: {}", e.getMessage(), e);
		return ResponseEntity.status(500).body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}