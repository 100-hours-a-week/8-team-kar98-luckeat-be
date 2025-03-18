package com.luckeat.luckeatbackend.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

	@NotBlank(message = "이메일은 필수입니다")
	@Email(message = "유효한 이메일 형식이 아닙니다")
	private String email;
	
	@NotBlank(message = "비밀번호는 필수입니다")
	private String password;
}