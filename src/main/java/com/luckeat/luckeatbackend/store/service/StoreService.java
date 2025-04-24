package com.luckeat.luckeatbackend.store.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

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
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

	private static final Logger logger = LoggerFactory.getLogger(StoreService.class);
	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final CategoryRepository categoryRepository;

	public List<StoreResponseDto> getStoresByCategory(Long categoryId) {
		return storeRepository.findAllByCategoryId(categoryId).stream().filter(store -> store.getDeletedAt() == null)
				.map(StoreResponseDto::fromEntity).toList();
	}

	public List<StoreResponseDto> getStoresByName(String storeName) {
		return storeRepository.findByStoreNameContainingAndDeletedAtIsNull(storeName).stream()
				.map(StoreResponseDto::fromEntity).toList();
	}

	/**
	 * 가게 ID로 가게 정보를 조회합니다.
	 * 캐시를 적용하여 성능을 향상시켰습니다.
	 */
	@Cacheable(value = "storeDetails", key = "#storeId")
	public StoreResponseDto getStoreById(Long storeId) {
		logger.debug("캐시 미스: DB에서 가게 정보 조회 - storeId={}", storeId);
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));
		return StoreResponseDto.fromEntity(store);
	}

	/**
	 * 가게 ID로 가게 상세 정보를 조회합니다.
	 * 캐시를 적용하여 성능을 향상시켰습니다.
	 */
	@Cacheable(value = "storeDetailsFull", key = "#storeId")
	public StoreDetailResponseDto getStoreDetailById(Long storeId) {
		logger.debug("캐시 미스: DB에서 가게 상세 정보 조회 - storeId={}", storeId);
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

	/**
	 * 새로운 가게를 생성합니다.
	 * 가게 생성 시 전체 가게 목록 캐시를 삭제합니다.
	 */
	@Transactional
	@CacheEvict(value = "stores", allEntries = true)
	public void createStore(StoreRequestDto request) {
		// 현재 인증된 사용자 ID 가져오기
		Long userId = getCurrentUserId();

		
		Store store = request.toEntity(userId);
		
		// 가게 이름을 사용하여 고유한 해시 생성
		String hashInput = store.getStoreName();
		// Google Place ID가 있으면 추가
		if (store.getGooglePlaceId() != null) {
			hashInput += store.getGooglePlaceId();
		}
		
		String url = generateSha256Hash(hashInput);
		store.setStoreUrl(url);
		storeRepository.save(store);
		
		logger.info("새 가게 생성 - 캐시 삭제됨: 가게 ID={}, 가게 이름={}", store.getId(), store.getStoreName());
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

	/**
	 * 가게 정보를 업데이트합니다.
	 * 업데이트 시 관련된 캐시를 모두 삭제합니다.
	 */
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "stores", allEntries = true),
		@CacheEvict(value = "storeDetails", key = "#storeId"),
		@CacheEvict(value = "storeDetailsFull", key = "#storeId")
	})
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
		logger.info("가게 정보 업데이트 - 캐시 삭제됨: storeId={}", storeId);
	}

	/**
	 * 가게를 삭제합니다.
	 * 삭제 시 관련된 캐시를 모두 삭제합니다.
	 */
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "stores", allEntries = true),
		@CacheEvict(value = "storeDetails", key = "#storeId"),
		@CacheEvict(value = "storeDetailsFull", key = "#storeId")
	})
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
		store.setDeletedAt(LocalDateTime.now());
		storeRepository.save(store);
		logger.info("가게 삭제 - 캐시 삭제됨: storeId={}", storeId);
	}

	/**
	 * 가게의 평균 평점을 업데이트합니다.
	 * 업데이트 시 관련된 캐시를 삭제합니다.
	 */
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "stores", allEntries = true),
		@CacheEvict(value = "storeDetails", key = "#storeId"),
		@CacheEvict(value = "storeDetailsFull", key = "#storeId")
	})
	public void updateAverageRating(Long storeId, float averageRating) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new StoreNotFoundException("스토어를 찾을 수 없습니다."));
		
		store.setAvgRating(averageRating); // avgRating으로 변경
		storeRepository.save(store);
		logger.info("가게 평점 업데이트 - 캐시 삭제됨: storeId={}, newRating={}", storeId, averageRating);
	}

	// Define a simple inner class (or record) to hold the result and timing
	public record StoreQueryResult(Page<StoreResponseDto> page, long queryExecutionTimeMs) {}

	/**
	 * 다양한 필터 조건을 기반으로 가게 목록을 검색하는 메서드 (시간 측정 포함)
	 * Redis 캐싱 적용: 동일한 검색 조건에 대해 캐시된 결과를 반환합니다.
	 *
	 * @return 필터링 및 정렬된 가게 목록 DTO와 DB 쿼리 실행 시간
	 */
	@Cacheable(value = "stores", key = "T(com.luckeat.luckeatbackend.store.service.StoreService).generateCacheKey(#sort, #isDiscountOpen, #page, #size, #categoryId)", 
			   condition = "#storeName == null && (#lat == null || #lng == null)")
	public StoreQueryResult getStores(Double lat, Double lng, Double radius, String sort,
									  String storeName, Boolean isDiscountOpen, int page, int size, int categoryId) {
		// 캐시된 결과가 있으면 이 로직은 실행되지 않고 캐시된 StoreQueryResult가 반환됩니다.
		logger.info("캐시 미스: DB에서 가게 목록 조회 - 파라미터: lat={}, lng={}, radius={}, sort={}, storeName={}, isDiscountOpen={}, page={}, size={}, categoryId={}",
				lat, lng, radius, sort, storeName, isDiscountOpen, page, size, categoryId);

		long serviceStartTimeNano = System.nanoTime(); // Use nanoTime for overall service timing
		Page<Store> storesPage;
		Pageable pageable = PageRequest.of(page, size);
		long queryExecutionTimeMs = 0; // Initialize query time in ms

		try {
			// DB 쿼리 실행 시간 측정 시작
			long queryStartTimeNano = System.nanoTime(); // Use nanoTime

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

			long queryEndTimeNano = System.nanoTime(); // Use nanoTime
			queryExecutionTimeMs = (queryEndTimeNano - queryStartTimeNano) / 1_000_000; // Convert ns to ms
			logger.info("DB 쿼리 실행 완료 - 실행 시간: {}ms, 조회된 데이터 수: {}", queryExecutionTimeMs, storesPage.getNumberOfElements());

			// DTO 변환 시간 측정 시작
			long mappingStartTimeNano = System.nanoTime();
			Page<StoreResponseDto> resultPage = storesPage.map(StoreResponseDto::fromEntity);
			long mappingEndTimeNano = System.nanoTime();
			long mappingExecutionTimeMs = (mappingEndTimeNano - mappingStartTimeNano) / 1_000_000; // Convert ns to ms

			long totalServiceTimeMs = (System.nanoTime() - serviceStartTimeNano) / 1_000_000; // Calculate total service time in ms
			logger.info("가게 목록 조회 서비스 완료 - 총 실행 시간: {}ms, DB 쿼리 시간: {}ms, DTO 변환 시간: {}ms",
					totalServiceTimeMs, queryExecutionTimeMs, mappingExecutionTimeMs);

			return new StoreQueryResult(resultPage, queryExecutionTimeMs); // Return DTO with page and query time

		} catch (Exception e) {
			logger.error("가게 목록 조회 서비스 오류 발생: {}", e.getMessage(), e);
			throw e;
		}
	}

	// 정적 캐시 키 생성 메서드 (EL 표현식에서 사용)
	public static String generateCacheKey(String sort, Boolean isDiscountOpen, int page, int size, int categoryId) {
		return String.format("%s-%s-%d-%d-%d",
			sort == null ? "null" : sort,
			isDiscountOpen == null ? "null" : isDiscountOpen.toString(),
			page, size, categoryId);
	}

	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			throw new StoreUnauthenticatedException("로그인이 필요합니다.");
		}

		String email = authentication.getName();
		User user = userRepository.findByEmailAndDeletedAtIsNull(email)
				.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
		return user.getId();
	}

	public MyStoreResponseDto getMyStore() {
		// 현재 인증된 사용자 ID 가져오기
		Long userId = getCurrentUserId();

		// 사용자의 가게 정보 조회
		Store store = storeRepository.findByUserIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new StoreNotFoundException("등록된 가게가 없습니다."));

		return MyStoreResponseDto.fromEntity(store);
	}
}