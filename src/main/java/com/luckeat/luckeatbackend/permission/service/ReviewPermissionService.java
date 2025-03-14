package com.luckeat.luckeatbackend.permission.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.permission.dto.PermissionRequestDto;
import com.luckeat.luckeatbackend.permission.dto.PermissionResponseDto;
import com.luckeat.luckeatbackend.permission.model.ReviewPermission;
import com.luckeat.luckeatbackend.permission.repository.ReviewPermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewPermissionService {

	private final ReviewPermissionRepository permissionRepository;

	public List<PermissionResponseDto> getAllPermissions() {
		return permissionRepository.findAll().stream().map(PermissionResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	public Optional<PermissionResponseDto> getPermissionById(Long id) {
		return permissionRepository.findById(id).map(PermissionResponseDto::fromEntity);
	}

	public Optional<ReviewPermission> getPermission(Long userId, Long storeId) {
		return permissionRepository.findByUserIdAndStoreId(userId, storeId);
	}

	public boolean hasPermission(Long userId, Long storeId) {
		return permissionRepository.findByUserIdAndStoreId(userId, storeId).isPresent();
	}

	@Transactional
	public PermissionResponseDto createPermission(PermissionRequestDto requestDto) {
		// 입력값 검증
		if (requestDto.getUserId() == null) {
			throw new IllegalArgumentException("사용자 ID는 필수입니다");
		}

		if (requestDto.getStoreId() == null) {
			throw new IllegalArgumentException("스토어 ID는 필수입니다");
		}

		Optional<ReviewPermission> existingPermission = permissionRepository
				.findByUserIdAndStoreId(requestDto.getUserId(), requestDto.getStoreId());

		if (existingPermission.isPresent()) {
			return PermissionResponseDto.fromEntity(existingPermission.get());
		} else {
			ReviewPermission permission = ReviewPermission.builder().userId(requestDto.getUserId())
					.storeId(requestDto.getStoreId()).build();
			return PermissionResponseDto.fromEntity(permissionRepository.save(permission));
		}
	}

	@Transactional
	public void deletePermission(Long id) {
		permissionRepository.deleteById(id);
	}

	@Transactional
	public void revokePermission(Long userId, Long storeId) {
		Optional<ReviewPermission> existingPermission = permissionRepository.findByUserIdAndStoreId(userId, storeId);
		existingPermission.ifPresent(permissionRepository::delete);
	}
}
