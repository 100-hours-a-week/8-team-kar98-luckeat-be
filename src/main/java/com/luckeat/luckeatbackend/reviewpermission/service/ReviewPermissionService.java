package com.luckeat.luckeatbackend.reviewpermission.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.common.exception.permission.PermissionAlreadyExistsException;
import com.luckeat.luckeatbackend.common.exception.permission.PermissionForbiddenException;
import com.luckeat.luckeatbackend.common.exception.permission.PermissionNotFoundException;
import com.luckeat.luckeatbackend.common.exception.store.StoreNotFoundException;
import com.luckeat.luckeatbackend.common.exception.user.UnauthenticatedException;
import com.luckeat.luckeatbackend.common.exception.user.UserNotFoundException;
import com.luckeat.luckeatbackend.reviewpermission.dto.PermissionRequestDto;
import com.luckeat.luckeatbackend.reviewpermission.dto.PermissionResponseDto;
import com.luckeat.luckeatbackend.reviewpermission.model.ReviewPermission;
import com.luckeat.luckeatbackend.reviewpermission.repository.ReviewPermissionRepository;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;
import com.luckeat.luckeatbackend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewPermissionService {

	private final ReviewPermissionRepository permissionRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	/**
	 * 모든 권한 목록을 조회합니다 (소프트 삭제된 항목 제외)
	 */
	public List<PermissionResponseDto> getAllPermissions() {
		return permissionRepository.findByDeletedAtIsNull().stream()
				.map(PermissionResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	/**
	 * 특정 ID의 권한을 조회합니다.
	 * 
	 * @param id 조회할 권한 ID
	 * @return 권한 정보 (Optional)
	 */
	public Optional<ReviewPermission> getPermissionById(Long id) {
		return permissionRepository.findByIdAndDeletedAtIsNull(id);
	}

	/**
	 * 사용자 ID와 스토어 ID로 권한을 조회합니다 (소프트 삭제된 항목 제외)
	 */
	public Optional<ReviewPermission> getPermission(Long userId, Long storeId) {
		return permissionRepository.findByUserIdAndStoreIdAndDeletedAtIsNull(userId, storeId);
	}

	/**
	 * 사용자가 특정 스토어에 대한 권한을 가지고 있는지 확인합니다 (소프트 삭제된 항목 제외)
	 */
	public boolean hasPermission(Long userId, Long storeId) {
		return permissionRepository.existsByUserIdAndStoreIdAndDeletedAtIsNull(userId, storeId);
	}

	/**
	 * 새로운 권한을 생성합니다
	 * @throws UserNotFoundException 사용자가 존재하지 않는 경우
	 * @throws StoreNotFoundException 스토어가 존재하지 않는 경우
	 * @throws PermissionAlreadyExistsException 이미 권한이 존재하는 경우
	 */
	@Transactional
	public PermissionResponseDto createPermission(PermissionRequestDto requestDto) {
		// 입력값 검증
		Long currentUserId = getCurrentUserId();

		// 타겟 사용자가 존재하는지 확인
	userRepository.findById(requestDto.getTargetUserId())
		.orElseThrow(() -> new PermissionNotFoundException("존재하지 않는 사용자입니다"));
		
		// 현재 사용자가 소유한 모든 스토어 조회
		List<Store> userStores = storeRepository.findAllByUserId(currentUserId);
		
		// 요청된 스토어가 사용자의 소유 스토어인지 확인
		boolean isOwner = userStores.stream()
			.anyMatch(store -> store.getId().equals(requestDto.getStoreId()));
		
		if (!isOwner) {
			throw new PermissionForbiddenException("스토어 소유자만 권한을 부여할 수 있습니다");
		}
		
		// 중복 권한 확인
		Optional<ReviewPermission> existingPermission = permissionRepository
				.findByUserIdAndStoreIdAndDeletedAtIsNull(requestDto.getTargetUserId(), requestDto.getStoreId());
		
		if (existingPermission.isPresent()) {
			throw new PermissionAlreadyExistsException("이미 권한이 존재합니다");
		}
		
		// 권한 생성 및 저장
		ReviewPermission permission = ReviewPermission.builder()
				.userId(requestDto.getTargetUserId())
				.storeId(requestDto.getStoreId())
				.build();
		
		ReviewPermission savedPermission = permissionRepository.save(permission);
		return PermissionResponseDto.fromEntity(savedPermission);
	}

	


	/**
 * 현재 로그인한 사용자의 특정 스토어에 대한 권한을 삭제합니다 (소프트 삭제).
 * @param storeId 권한을 삭제할 스토어 ID
 * @throws PermissionNotFoundException 권한이 존재하지 않는 경우
 */
@Transactional
public void deleteMyPermission(Long storeId) {
    // 현재 인증된 사용자 ID 가져오기
    Long currentUserId = getCurrentUserId();
    
    // 현재 사용자의 권한 조회
    ReviewPermission permission = permissionRepository.findByUserIdAndStoreIdAndDeletedAtIsNull(currentUserId, storeId)
            .orElseThrow(() -> new PermissionNotFoundException("해당 권한 정보를 찾을 수 없습니다"));
    
    // 소프트 삭제 처리
    permission.setDeletedAt(LocalDateTime.now());
    permissionRepository.save(permission);
}

	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			log.error("인증되지 않은 사용자 접근: {}", authentication);
			throw new UnauthenticatedException();
		}

		// 이메일로 사용자 조회하여 ID 반환
		String email = authentication.getName();
		return userRepository.findByEmailAndDeletedAtIsNull(email)
				.orElseThrow(() -> new UserNotFoundException()).getId();
	}
} 