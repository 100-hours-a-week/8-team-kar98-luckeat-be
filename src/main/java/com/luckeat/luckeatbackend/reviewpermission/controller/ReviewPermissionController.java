package com.luckeat.luckeatbackend.reviewpermission.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.luckeat.luckeatbackend.common.exception.permission.PermissionNotFoundException;
import com.luckeat.luckeatbackend.reviewpermission.dto.PermissionRequestDto;
import com.luckeat.luckeatbackend.reviewpermission.dto.PermissionResponseDto;
import com.luckeat.luckeatbackend.reviewpermission.service.ReviewPermissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 리뷰 작성 권한 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "리뷰 권한 API", description = "리뷰 작성 권한 관련 API 목록")
public class ReviewPermissionController {

	private final ReviewPermissionService permissionService;

	/**
	 * 모든 리뷰 작성 권한을 조회합니다.
	 *
	 * @return 성공 시 200 OK
	 */
	@Operation(summary = "모든 리뷰 작성 권한 조회", description = "모든 리뷰 작성 권한을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 작성 권한 목록 조회 성공")
	})
	@GetMapping
	public ResponseEntity<List<PermissionResponseDto>> getAllPermissions() {
		List<PermissionResponseDto> permissions = permissionService.getAllPermissions();
		return ResponseEntity.ok(permissions);
	}

	/**
	 * 특정 ID의 권한을 조회합니다.
	 *
	 * @param id 조회할 권한 ID
	 * @return 권한 정보
	 * @throws PermissionNotFoundException 해당 ID의 권한이 없는 경우 발생
	 */
	@Operation(summary = "특정 권한 조회", description = "특정 ID의 권한을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "권한 조회 성공"),
		@ApiResponse(responseCode = "404", description = "권한을 찾을 수 없음")
	})
	@GetMapping("/{permission_id}")
	public ResponseEntity<PermissionResponseDto> getPermissionById(@PathVariable("permission_id") Long id) {
		PermissionResponseDto permission = permissionService.getPermissionById(id)
				.map(PermissionResponseDto::fromEntity)
				.orElseThrow(() -> new PermissionNotFoundException("권한 정보를 찾을 수 없습니다: " + id));
		return ResponseEntity.ok(permission);
	}

	/**
	 * 새로운 리뷰 작성 권한을 생성합니다.
	 *
	 * @param requestDto 권한 생성 요청 정보
	 * @return 생성 성공 시 201 Created
	 */
	@Operation(summary = "리뷰 작성 권한 생성", description = "새로운 리뷰 작성 권한을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "리뷰 작성 권한 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PostMapping
	public ResponseEntity<Void> createPermission(@Valid @RequestBody PermissionRequestDto requestDto) {
		permissionService.createPermission(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 현재 로그인한 사용자의 특정 스토어에 대한 권한을 삭제합니다
	 * @param storeId 권한을 삭제할 스토어 ID
	 * @return 성공 응답
	 */
	@Operation(summary = "리뷰 작성 권한 삭제", description = "현재 로그인한 사용자의 특정 스토어에 대한 권한을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 작성 권한 삭제 성공")
	})
	@DeleteMapping
	public ResponseEntity<Void> deleteMyPermission(@RequestParam Long storeId) {
		permissionService.deleteMyPermission(storeId);
		return ResponseEntity.ok().build();
	}

	/**
	 * 사용자가 특정 스토어에 대한 리뷰 작성 권한이 있는지 확인합니다.
	 *
	 * @param userId 사용자 ID
	 * @param storeId 스토어 ID
	 * @return 권한 있으면 200 OK, 없으면 403 Forbidden
	 */
	@Operation(summary = "리뷰 작성 권한 확인", description = "사용자가 특정 스토어에 대한 리뷰 작성 권한이 있는지 확인합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 작성 권한 있음"),
		@ApiResponse(responseCode = "403", description = "리뷰 작성 권한 없음")
	})
	@GetMapping("/check")
	public ResponseEntity<Void> checkPermission(@RequestParam Long userId, @RequestParam Long storeId) {
		boolean hasPermission = permissionService.hasPermission(userId, storeId);
		
		if (hasPermission) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
}
