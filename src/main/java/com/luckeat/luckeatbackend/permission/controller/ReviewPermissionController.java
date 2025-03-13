package com.luckeat.luckeatbackend.permission.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.luckeat.luckeatbackend.permission.dto.MessageResponseDto;
import com.luckeat.luckeatbackend.permission.dto.PermissionListResponseDto;
import com.luckeat.luckeatbackend.permission.dto.PermissionRequestDto;
import com.luckeat.luckeatbackend.permission.dto.PermissionResponseDto;
import com.luckeat.luckeatbackend.permission.service.ReviewPermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Slf4j
public class ReviewPermissionController {

	private final ReviewPermissionService permissionService;

	@GetMapping
	public ResponseEntity<PermissionListResponseDto> getAllPermissions() {
		List<PermissionResponseDto> permissions = permissionService.getAllPermissions();

		PermissionListResponseDto response = PermissionListResponseDto.builder().message("권한 데이터 조회 성공")
				.permissions(permissions).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{permission_id}")
	public ResponseEntity<?> getPermissionById(@PathVariable("permission_id") Long id) {
		if (permissionService.getPermissionById(id).isPresent()) {
			PermissionResponseDto permission = permissionService.getPermissionById(id).get();

			PermissionListResponseDto response = PermissionListResponseDto.builder().message("권한 데이터 조회 성공")
					.permissions(List.of(permission)).build();

			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageResponseDto.error("리뷰 작성 권한 정보를 찾을 수 없음"));
		}
	}

	@PostMapping
	public ResponseEntity<MessageResponseDto> createPermission(@RequestBody PermissionRequestDto requestDto) {
		try {
			permissionService.createPermission(requestDto);
			return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponseDto.success("리뷰 작성 권한 부여 성공"));
		} catch (IllegalArgumentException e) {
			log.error("권한 생성 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageResponseDto.error(e.getMessage()));
		} catch (Exception e) {
			log.error("권한 생성 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageResponseDto.error("서버 내부 오류"));
		}
	}

	@DeleteMapping("/{permission_id}")
	public ResponseEntity<MessageResponseDto> deletePermission(@PathVariable("permission_id") Long id) {
		if (permissionService.getPermissionById(id).isPresent()) {
			permissionService.deletePermission(id);
			return ResponseEntity.ok(MessageResponseDto.success("권한 정보 삭제 성공"));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageResponseDto.error("리뷰 작성 권한 정보를 찾을 수 없음"));
		}
	}

	@GetMapping("/check")
	public ResponseEntity<MessageResponseDto> checkPermission(@RequestParam Long userId, @RequestParam Long storeId) {
		boolean hasPermission = permissionService.hasPermission(userId, storeId);

		if (hasPermission) {
			return ResponseEntity.ok(MessageResponseDto.success("리뷰 작성 권한이 있습니다"));
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(MessageResponseDto.error("리뷰 작성 권한이 없습니다"));
		}
	}
}