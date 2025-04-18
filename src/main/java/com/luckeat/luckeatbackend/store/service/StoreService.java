package com.luckeat.luckeatbackend.store.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.category.repository.CategoryRepository;
import com.luckeat.luckeatbackend.common.exception.store.StoreForbiddenException;
import com.luckeat.luckeatbackend.common.exception.store.StoreNotFoundException;
import com.luckeat.luckeatbackend.common.exception.store.StoreUnauthenticatedException;
import com.luckeat.luckeatbackend.common.exception.user.UserNotFoundException;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.repository.ProductRepository;
import com.luckeat.luckeatbackend.review.dto.ReviewResponseDto;
import com.luckeat.luckeatbackend.review.repository.ReviewRepository;
import com.luckeat.luckeatbackend.store.dto.MyStoreResponseDto;
import com.luckeat.luckeatbackend.store.dto.StoreDetailResponseDto;
import com.luckeat.luckeatbackend.store.dto.StoreRequestDto;
import com.luckeat.luckeatbackend.store.dto.StoreResponseDto;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;
import com.luckeat.luckeatbackend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final CategoryRepository categoryRepository;

	public List<StoreResponseDto> getStoresByCategory(Long categoryId) {
		return storeRepository.findAllByCategoryId(categoryId).stream().filter(store -> store.getDeletedAt() == null)
				.map(StoreResponseDto::fromEntity).toList();
	}

	public Page<StoreResponseDto> getAllStores(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Store> storePage = storeRepository.findAllByDeletedAtIsNull(pageable);
		return storePage.map(StoreResponseDto::fromEntity);
	}

	public List<StoreResponseDto> getStoresByName(String storeName) {
		return storeRepository.findByStoreNameContainingAndDeletedAtIsNull(storeName).stream()
				.map(StoreResponseDto::fromEntity).toList();
	}

	public StoreResponseDto getStoreById(Long storeId) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));
		return StoreResponseDto.fromEntity(store);
	}

	public StoreDetailResponseDto getStoreDetailById(Long storeId) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));

		// 가게와 연관된 모든 제품을 한 번에 조회 (매핑이 설정되어 있어 자동으로 조인)
		List<Product> activeProducts = store.getProducts().stream().filter(product -> product.getDeletedAt() == null)
				.collect(Collectors.toList());

		// ReviewRepository를 사용하여 가게에 대한 리뷰 조회
    List<ReviewResponseDto> reviews = reviewRepository.findByStoreIdAndDeletedAtIsNull(storeId).stream()
            .map(ReviewResponseDto::fromEntity) // ReviewResponseDto로 변환
            .collect(Collectors.toList()); // List<ReviewResponseDto>로 변환

		return StoreDetailResponseDto.fromEntity(store, activeProducts, reviews); // 리뷰 포함하여 반환
	}

	@Transactional
	public void createStore(StoreRequestDto request) {
		// 현재 인증된 사용자 ID 가져오기
		Long userId = getCurrentUserId();

		
		Store store = request.toEntity(userId);
		
		// 가게 이름과 Google Place ID를 조합하여 고유한 해시 생성
		String hashInput = store.getStoreName() + store.getGooglePlaceId();
		String url = generateSha256Hash(hashInput);
		store.setStoreUrl(url);
		storeRepository.save(store);
	}

	/**
	 * 문자열에 대한 SHA-256 해시를 생성하는 메서드
	 * 
	 * @param input 해시할 입력 문자열
	 * @return 16진수 형태의 SHA-256 해시 문자열 (첫 8자리만 사용)
	 */
	private String generateSha256Hash(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			
			// 16진수 문자열로 변환
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			
			// URL에 사용하기 위해 첫 8자리만 반환 (충분한 고유성 보장)
			return hexString.toString().substring(0, 8);
		} catch (NoSuchAlgorithmException e) {
			// SHA-256 알고리즘을 사용할 수 없는 경우 기존 방식으로 폴백
			// hashCode()를 사용하지만 8자리로 일관된 길이 유지
			String hashCodeHex = Integer.toHexString(input.hashCode());
			
			// 8자리 미만인 경우 앞에 0을 채워 8자리로 맞춤
			if (hashCodeHex.length() < 8) {
				StringBuilder paddedHex = new StringBuilder();
				for (int i = 0; i < 8 - hashCodeHex.length(); i++) {
					paddedHex.append('0');
				}
				paddedHex.append(hashCodeHex);
				return paddedHex.toString();
			}
			
			// 8자리 이상인 경우 앞 8자리만 사용
			return hashCodeHex.substring(0, Math.min(hashCodeHex.length(), 8));
		}
	}

	@Transactional
	public void updateStore(Long storeId, StoreRequestDto request) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));

		// 현재 인증된 사용자 ID 가져오기
		Long userId = getCurrentUserId();

		// 권한 확인
		if (!store.getUserId().equals(userId)) {
			throw new StoreForbiddenException("해당 가게에 대한 수정 권한이 없습니다.");
		}

		// 기존 ID와 사용자 ID 유지
		Store updatedStore = request.toEntity(userId);
		updatedStore.setId(storeId);
		updatedStore.setShareCount(store.getShareCount()); // 공유 카운트 유지

		storeRepository.save(updatedStore);
	}

	@Transactional
	public void deleteStore(Long storeId) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));

		// 현재 인증된 사용자 ID 가져오기
		Long userId = getCurrentUserId();

		// 권한 확인
		if (!store.getUserId().equals(userId)) {
			throw new StoreForbiddenException("해당 가게에 대한 삭제 권한이 없습니다.");
		}

		// 논리적 삭제를 위해 deletedAt 필드 업데이트
		store.setDeletedAt(java.time.LocalDateTime.now());
		storeRepository.save(store);
	}
	 @Transactional
    public void updateAverageRating(Long storeId, float averageRating) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException("스토어를 찾을 수 없습니다."));
        
        store.setAvgRating(averageRating); // avgRating으로 변경
        storeRepository.save(store);
    }

	/**
	 * 다양한 필터 조건을 기반으로 가게 목록을 검색하는 메서드
	 *
	 * @param lat            위도 (선택적)
	 * @param lng            경도 (선택적)
	 * @param radius         검색 반경 (km) (선택적)
	 * @param sort           정렬 기준 (distance: 거리순, share: 공유순) (선택적)
	 * @param storeName      가게 이름 검색어 (선택적)
	 * @param isDiscountOpen 마감 할인 중인 가게만 필터링 (선택적)
	 * @return 필터링 및 정렬된 가게 목록 DTO
	 */
	public Page<StoreResponseDto> getStores(Double lat, Double lng, Double radius, String sort,
											String storeName, Boolean isDiscountOpen, int page, int size, int categoryId) {

		Page<Store> storesPage;
    	Pageable pageable = PageRequest.of(page, size);
    
		// 위치 기반 검색이 필요한 경우
		if (lat != null && lng != null) {
			storesPage = storeRepository.findStoresWithLocation(
					categoryId, storeName, isDiscountOpen, lat, lng, radius, sort, pageable);
		} 
		// 위치 기반 검색이 필요 없는 경우
		else {
			storesPage = storeRepository.findStoresWithoutLocation(
					categoryId, storeName, isDiscountOpen, sort, pageable);
		}
		
		return storesPage.map(StoreResponseDto::fromEntity);
	}	
	
	/**
	 * 현재 인증된 사용자의 ID를 가져오는 메소드
	 * 
	 * @return 인증된 사용자의 ID
	 */
	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			log.error("인증되지 않은 사용자 접근: {}", authentication);
			throw new StoreUnauthenticatedException();
		}

		// 이메일로 사용자 조회하여 ID 반환
		String email = authentication.getName();
		return userRepository.findByEmailAndDeletedAtIsNull(email)
				.orElseThrow(() -> new UserNotFoundException()).getId();
	}

	public MyStoreResponseDto getMyStore() {
		Long userId = getCurrentUserId();
		
		Store store = storeRepository.findByUserIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> {
					return new StoreNotFoundException("가게를 찾을 수 없습니다.");
				});
		
		log.info("찾은 가게 정보: id={}, name={}", store.getId(), store.getStoreName());
		return MyStoreResponseDto.fromEntity(store);
	}
}