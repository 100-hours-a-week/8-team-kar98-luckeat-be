package com.luckeat.luckeatbackend.common.exception;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.luckeat.luckeatbackend.common.exception.base.CustomException;
import com.luckeat.luckeatbackend.common.exception.base.FileUploadException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 사용자 정의 예외 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		log.error("CustomException: {}", e.getMessage());
		return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorResponse(e.getErrorCode()));
	}

	// 인증 관련 예외 처리
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
		log.error("AuthenticationException: {}", e.getMessage());
		return ResponseEntity.status(401).body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
	}

	// 권한 관련 예외 처리
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
		log.error("AccessDeniedException: {}", e.getMessage());
		return ResponseEntity.status(403).body(new ErrorResponse(ErrorCode.FORBIDDEN));
	}

	// 로그인 실패 예외 처리
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
		log.error("BadCredentialsException: {}", e.getMessage());
		return ResponseEntity.status(401).body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
   public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
       log.error("MethodNotAllowedException: {}", ex.getMessage());
       return ResponseEntity
               .status(405)
               .body(new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED));
   }

	// 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("ValidationException: {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(400).body(new ValidationErrorResponse(ErrorCode.VALIDATION_ERROR, errors));
    }

    // 바인딩 예외 처리
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationErrorResponse> handleBindException(BindException e) {
        log.error("BindException: {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(400).body(new ValidationErrorResponse(ErrorCode.VALIDATION_ERROR, errors));
    }

	// 잘못된 요청 파라미터 타입 예외 처리
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException e) {
		log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());
		return ResponseEntity.status(400).body(new ErrorResponse(ErrorCode.BAD_REQUEST));
	}

	// 데이터베이스 접근 예외 처리
	@ExceptionHandler({SQLException.class, DataAccessException.class})
	public ResponseEntity<ErrorResponse> handleDatabaseException(Exception e) {
		log.error("DatabaseException: {}", e.getMessage());
		return ResponseEntity.status(500).body(new ErrorResponse(ErrorCode.DATABASE_ERROR));
	}

	// 데이터 무결성 위반 예외 처리 (중복 키 등)
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    log.error("DataIntegrityViolationException: {}", e.getMessage());
    
    String errorMessage = e.getMessage();
    
    // 닉네임 중복인 경우
    if (errorMessage.contains("nickname")) {
        return ResponseEntity.status(409).body(new ErrorResponse(ErrorCode.NICKNAME_DUPLICATE));
    }
    // 이메일 중복인 경우
    else if (errorMessage.contains("email")) {
        return ResponseEntity.status(409).body(new ErrorResponse(ErrorCode.EMAIL_DUPLICATE));
    }
    // 그 외 데이터베이스 오류
    else {
        return ResponseEntity.status(500).body(new ErrorResponse(ErrorCode.DATABASE_ERROR));
    }
}

	// 그 외 모든 예외 처리
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("Exception: {}", e.getMessage(), e);
		return ResponseEntity.status(500).body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
	}

	@ExceptionHandler(FileUploadException.class)
	public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException e) {
		log.error("FileUploadException: {}", e.getMessage());
		return ResponseEntity.status(400).body(new ErrorResponse(ErrorCode.FILE_UPLOAD_ERROR, e.getMessage()));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		log.error("MaxUploadSizeExceededException: {}", e.getMessage());
		return ResponseEntity.status(400).body(new ErrorResponse(ErrorCode.FILE_UPLOAD_ERROR, "파일 크기가 너무 큽니다"));
	}
}