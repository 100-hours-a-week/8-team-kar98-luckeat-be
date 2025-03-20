package com.luckeat.luckeatbackend.reviewpermission.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestDto {

	@NotNull(message = "스토어 ID는 필수입니다")
	@Schema(description = "스토어 ID", example = "123")
	private Long storeId;
	
	@NotNull(message = "권한을 부여할 사용자 ID는 필수입니다")
	@Schema(description = "권한을 부여할 사용자 ID", example = "456")
	private Long targetUserId;
}