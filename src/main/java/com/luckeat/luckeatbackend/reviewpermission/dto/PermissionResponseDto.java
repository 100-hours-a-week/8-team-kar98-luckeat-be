package com.luckeat.luckeatbackend.reviewpermission.dto;

import java.time.LocalDateTime;

import com.luckeat.luckeatbackend.reviewpermission.model.ReviewPermission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDto {

	private Long permissionId;
	private Long storeId;
	private Long userId;
	private LocalDateTime createdAt;

	public static PermissionResponseDto fromEntity(ReviewPermission permission) {
		return PermissionResponseDto.builder().permissionId(permission.getId()).storeId(permission.getStoreId())
				.userId(permission.getUserId()).createdAt(permission.getCreatedAt()).build();
	}
}