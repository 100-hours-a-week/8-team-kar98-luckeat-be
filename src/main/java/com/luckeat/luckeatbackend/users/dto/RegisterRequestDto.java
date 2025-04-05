package com.luckeat.luckeatbackend.users.dto;

import com.luckeat.luckeatbackend.users.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class RegisterRequestDto {
    
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    private String email;
    
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내여야 합니다.")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Schema(description = "Base64로 인코딩된 비밀번호 (8~20자, 문자와 숫자 포함)", example = "cGFzc3dvcmQxMjM=", required = true)
    private String password;
    
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 이내여야 합니다.")
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Schema(description = "사용자 닉네임", example = "홍길동", required = true)
    private String nickname;
    
    @NotNull(message = "역할은 필수입니다")
    @Schema(description = "사용자 역할 (BUYER, SELLER, ADMIN)", example = "BUYER", required = true)
    private User.Role role;
} 