package com.luckeat.luckeatbackend.reviewpermission.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestDto {

	@NotNull(message = "스토어 ID는 필수입니다")
	private Long storeId;
	
	@NotNull(message = "권한을 부여할 사용자 ID는 필수입니다")
	private Long targetUserId;
}