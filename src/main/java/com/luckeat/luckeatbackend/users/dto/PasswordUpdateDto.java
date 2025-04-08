package com.luckeat.luckeatbackend.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 수정 요청 DTO")
public class PasswordUpdateDto {

	@NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다.")
	@Schema(description = "현재 비밀번호", example = "currentPassword123", required = true)
	private String currentPassword;
	
	@Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내여야 합니다.")
	@NotBlank(message = "새 비밀번호는 필수 입력 항목입니다.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", 
			message = "비밀번호는 영문자와 숫자를 모두 포함해야 합니다.")
	@Schema(description = "새 비밀번호 (8~20자, 문자와 숫자 포함)", example = "newPassword123", required = true)
	private String newPassword;
	
	@NotBlank(message = "새 비밀번호 확인은 필수입니다")
	@Schema(description = "새 비밀번호 확인", example = "newPassword123", required = true)
	private String confirmPassword;
}