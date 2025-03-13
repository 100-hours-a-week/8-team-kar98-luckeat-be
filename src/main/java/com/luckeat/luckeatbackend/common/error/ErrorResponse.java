package com.luckeat.luckeatbackend.common.error;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

	private LocalDateTime timestamp = LocalDateTime.now();
	private String code;
	private String message;
	private List<FieldError> errors;
	private String path;

	private ErrorResponse(ErrorCode errorCode, String path) {
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
		this.errors = new ArrayList<>();
		this.path = path;
	}

	private ErrorResponse(ErrorCode errorCode, List<FieldError> errors, String path) {
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
		this.errors = errors;
		this.path = path;
	}

	public static ErrorResponse of(ErrorCode errorCode, String path) {
		return new ErrorResponse(errorCode, path);
	}

	public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult, String path) {
		return new ErrorResponse(errorCode, FieldError.of(bindingResult), path);
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class FieldError {
		private String field;
		private String value;
		private String reason;

		private FieldError(String field, String value, String reason) {
			this.field = field;
			this.value = value;
			this.reason = reason;
		}

		public static List<FieldError> of(BindingResult bindingResult) {
			List<FieldError> fieldErrors = new ArrayList<>();
			bindingResult.getFieldErrors().forEach(error -> {
				fieldErrors.add(new FieldError(error.getField(),
						error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
						error.getDefaultMessage()));
			});
			return fieldErrors;
		}
	}
}
