package com.luckeat.luckeatbackend.permission.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.permission.model.ReviewPermission;
import com.luckeat.luckeatbackend.permission.repository.ReviewPermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewPermissionService {

	private final ReviewPermissionRepository permissionRepository;

	public Optional<ReviewPermission> getPermission(Long userId, Long storeId) {
		return permissionRepository.findByUserIdAndStoreId(userId, storeId);
	}

	public boolean hasPermission(Long userId, Long storeId) {
		return permissionRepository.findByUserIdAndStoreId(userId, storeId).isPresent();
	}

	@Transactional
	public ReviewPermission grantPermission(Long userId, Long storeId) {
		Optional<ReviewPermission> existingPermission = permissionRepository.findByUserIdAndStoreId(userId, storeId);

		if (existingPermission.isPresent()) {
			return existingPermission.get();
		} else {
			ReviewPermission permission = ReviewPermission.builder()
					.userId(userId)
					.storeId(storeId)
					.build();
			return permissionRepository.save(permission);
		}
	}

	@Transactional
	public void revokePermission(Long userId, Long storeId) {
		Optional<ReviewPermission> existingPermission = permissionRepository.findByUserIdAndStoreId(userId, storeId);
		existingPermission.ifPresent(permissionRepository::delete);
	}
}