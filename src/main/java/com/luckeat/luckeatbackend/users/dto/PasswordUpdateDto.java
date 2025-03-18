package com.luckeat.luckeatbackend.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateDto {

	@NotBlank(message = "현재 비밀번호는 필수입니다")
	private String currentPassword;
	
	@NotBlank(message = "새 비밀번호는 필수입니다")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,20}$", 
			message = "비밀번호는 8자 이상 20자 이하이며, 영어 소문자와 숫자를 각각 최소 1개 이상 포함해야 합니다")
	private String newPassword;
	
	@NotBlank(message = "새 비밀번호 확인은 필수입니다")
	private String confirmPassword;
}