package com.luckeat.luckeatbackend.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

	@Email(message = "유효한 이메일 형식이 아닙니다.")
	@NotBlank(message = "이메일은 필수 입력 항목입니다.")
	@Schema(description = "사용자 이메일", example = "user123@example.com", required = true)
	private String email;
	
	@NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
	@Schema(description = "비밀번호", example = "password123", required = true)
	private String password;
}