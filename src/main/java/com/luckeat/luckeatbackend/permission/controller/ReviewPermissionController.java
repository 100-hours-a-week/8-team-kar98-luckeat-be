package com.luckeat.luckeatbackend.permission.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.luckeat.luckeatbackend.permission.model.ReviewPermission;
import com.luckeat.luckeatbackend.permission.service.ReviewPermissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class ReviewPermissionController {

	private final ReviewPermissionService permissionService;

	@GetMapping("/")
	public ResponseEntity<Boolean> checkPermission(@RequestParam Long userId, @RequestParam Long storeId) {
		return ResponseEntity.ok(permissionService.hasPermission(userId, storeId));
	}

	@GetMapping("/{permissoin_id}")
	public ResponseEntity<Boolean> checkUserPermission(@RequestParam Long userId, @RequestParam Long storeId) {
		return ResponseEntity.ok(permissionService.hasPermission(userId, storeId));
	}

	@PostMapping("/{permission_id}")
	public ResponseEntity<ReviewPermission> grantPermission(@RequestParam Long userId, @RequestParam Long storeId) {
		ReviewPermission permission = permissionService.grantPermission(userId, storeId);
		return ResponseEntity.status(HttpStatus.CREATED).body(permission);
	}

	@DeleteMapping("/{permission_id}")
	public ResponseEntity<?> revokePermission(@RequestParam Long userId, @RequestParam Long storeId) {
		permissionService.revokePermission(userId, storeId);
		return ResponseEntity.ok().build();
	}
}