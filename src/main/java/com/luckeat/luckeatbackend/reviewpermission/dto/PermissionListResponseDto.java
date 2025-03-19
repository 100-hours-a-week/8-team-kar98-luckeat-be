package com.luckeat.luckeatbackend.reviewpermission.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionListResponseDto {

	private List<PermissionResponseDto> permissions;
}