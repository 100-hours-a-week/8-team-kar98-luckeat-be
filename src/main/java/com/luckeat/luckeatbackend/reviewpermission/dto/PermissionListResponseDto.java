package com.luckeat.luckeatbackend.reviewpermission.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionListResponseDto {

	@Schema(description = "권한 목록", example = "[{...}, {...}]")
	private List<PermissionResponseDto> permissions;
}