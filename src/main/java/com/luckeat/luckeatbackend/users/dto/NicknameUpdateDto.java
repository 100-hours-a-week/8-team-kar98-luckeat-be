package com.luckeat.luckeatbackend.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "닉네임 수정 요청 DTO")
public class NicknameUpdateDto {

	@Size(min = 2, max = 10, message = "닉네임은 2~10자 이내여야 합니다.")
	@NotBlank(message = "닉네임은 필수 입력 항목입니다.")
	@Schema(description = "새 닉네임", example = "새로운닉네임", required = true)
	private String nickname;
}