package com.luckeat.luckeatbackend.users.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.luckeat.luckeatbackend.users.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 정보 응답 DTO")
public class UserInfoResponseDto {

	@Schema(description = "사용자 ID", example = "123")
	private Long id;
	
	@Schema(description = "사용자 이메일", example = "user@example.com")
	private String email;
	
	@Schema(description = "사용자 닉네임", example = "홍길동")
	private String nickname;
	
	@Schema(description = "사용자 역할", example = "BUYER")
	private User.Role role;
	
	@Schema(description = "생성 일시", example = "2025-03-20T10:15:30")
	private LocalDateTime createdAt;
	
	@Schema(description = "수정 일시", example = "2025-03-20T10:15:30")
	private LocalDateTime updatedAt;
	
	@Schema(description = "총 아낀 금액", example = "15000")
	private Integer totalSavedMoney;
	
	@Schema(description = "총 상품 갯수", example = "5")
	private Integer totalProductCount;

	public static UserInfoResponseDto fromEntity(User user) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

		return UserInfoResponseDto.builder().id(user.getId()).email(user.getEmail())
				.nickname(user.getNickname())
				.role(user.getRole())
				.createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt())
				.totalSavedMoney(user.getTotalSavedMoney())
				.totalProductCount(user.getTotalProductCount())
				.build();
	}
}