package com.luckeat.luckeatbackend.reviewpermission.dto;

import java.time.LocalDateTime;

import com.luckeat.luckeatbackend.reviewpermission.model.ReviewPermission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDto {

	@Schema(description = "권한 ID", example = "1")
	private Long permissionId;

	@Schema(description = "스토어 ID", example = "123")
	private Long storeId;

	@Schema(description = "사용자 ID", example = "456")
	private Long userId;

	@Schema(description = "권한 생성 시간", example = "2023-03-20T12:34:56")
	private LocalDateTime createdAt;

	public static PermissionResponseDto fromEntity(ReviewPermission permission) {
		return PermissionResponseDto.builder().permissionId(permission.getId()).storeId(permission.getStoreId())
				.userId(permission.getUserId()).createdAt(permission.getCreatedAt()).build();
	}
}