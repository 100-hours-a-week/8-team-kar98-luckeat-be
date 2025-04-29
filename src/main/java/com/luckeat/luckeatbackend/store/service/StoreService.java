package com.luckeat.luckeatbackend.store.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.luckeat.luckeatbackend.review.dto.ReviewResponseDto;
import com.luckeat.luckeatbackend.review.repository.ReviewRepository;
import com.luckeat.luckeatbackend.store.dto.MyStoreResponseDto;
import com.luckeat.luckeatbackend.store.dto.StoreDetailResponseDto;
import com.luckeat.luckeatbackend.store.dto.StoreRequestDto;
import com.luckeat.luckeatbackend.store.dto.StoreResponseDto;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;
import com.luckeat.luckeatbackend.store.dto.StoreListDto;
import com.luckeat.luckeatbackend.store.dto.StoreQueryResult;
import com.luckeat.luckeatbackend.common.util.SortUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

	private static final Logger logger = LoggerFactory.getLogger(StoreService.class);
	private final StoreRepository storeRepository;
	private final ReviewRepository reviewRepository;
	private final CategoryRepository categoryRepository;

	private static final String STORE_LIST_CACHE_PREFIX = "storeList::";

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

		// 가게와 연관된 모든 제품을 한 번에 조회 (Store 엔티티 내 @EntityGraph 가정)
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
		// 현재 인증된 사용자 ID 가져오기 (주석 처리됨)
		Long userId = getCurrentUserId(); // 이 메서드는 주석 처리되어 실제로는 호출 불가 상태여야 함
		// Long userId = 1L; // 임시 사용자 ID 또는 실제 로직 구현 필요

		Store store = request.toEntity(userId);

		storeRepository.save(store);

		logger.info("새 가게 생성 - 캐시 삭제됨: 가게 ID={}, 가게 이름={}", store.getId(), store.getStoreName());
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

		// 현재 인증된 사용자 ID 가져오기 (주석 처리됨)
		Long userId = getCurrentUserId(); // 이 메서드는 주석 처리되어 실제로는 호출 불가 상태여야 함
		// Long userId = 1L; // 임시 사용자 ID 또는 실제 로직 구현 필요

		// 권한 확인
		if (!store.getUserId().equals(userId)) {
			throw new StoreForbiddenException("해당 가게에 대한 수정 권한이 없습니다.");
		}

		// 기존 ID와 사용자 ID 유지
		Store updatedStore = request.toEntity(userId);
		updatedStore.setId(storeId);
		// updatedStore.setShareCount(store.getShareCount()); // Store 엔티티에 getShareCount 없으므로 제거

		storeRepository.save(updatedStore);
		logger.info("가게 정보 업데이트 - 캐시 삭제됨: storeId={}", storeId);
	}

	/**
	 * 가게를 삭제합니다. (논리적 삭제)
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

		// 현재 인증된 사용자 ID 가져오기 (주석 처리됨)
		Long userId = getCurrentUserId(); // 이 메서드는 주석 처리되어 실제로는 호출 불가 상태여야 함
		// Long userId = 1L; // 임시 사용자 ID 또는 실제 로직 구현 필요

		// 권한 확인
		if (!store.getUserId().equals(userId)) {
			throw new StoreForbiddenException("해당 가게에 대한 삭제 권한이 없습니다.");
		}

		// 논리적 삭제를 위해 deletedAt 필드 업데이트
		store.setDeletedAt(LocalDateTime.now());
		storeRepository.save(store);
		logger.info("가게 삭제 (논리적) - 캐시 삭제됨: storeId={}", storeId);
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
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("Store not found with id: " + storeId));
		store.setAvgRating(averageRating); // setAverageRating -> setAvgRating (엔티티 필드명 기준)
		storeRepository.save(store);
		logger.info("평균 평점 업데이트 - 캐시 삭제됨: storeId={}", storeId);
	}

	/**
	 * 가게 목록을 조회합니다. 페이징 및 정렬 기능을 포함합니다.
	 * 위치 기반 검색, 이름 검색, 할인 여부 필터링, 카테고리 필터링 기능을 제공합니다.
	 * 캐시를 적용하여 성능을 향상시켰습니다. 조건부 캐싱을 사용하여 다양한 검색 조건에 대응합니다.
	 *
	 * @param bypassCache true일 경우 캐시를 사용하지 않고 DB에서 직접 조회합니다.
	 * @return 가게 목록 검색 결과 (페이징 정보 포함)
	 */
	@Cacheable(value = "stores", key = "T(com.luckeat.luckeatbackend.store.service.StoreService).generateCacheKey(#sort, #isDiscountOpen, #page, #size, #categoryId)",
			   condition = "!#bypassCache && #storeName == null && (#lat == null || #lng == null)")
	public StoreQueryResult getStores(Double lat, Double lng, Double radius, String sort,
										String storeName, Boolean isDiscountOpen, int page, int size, int categoryId, boolean bypassCache) {

		if (bypassCache) {
			logger.info("캐시 우회: DB에서 직접 가게 목록 조회 수행");
		}

		long startTime = System.currentTimeMillis(); // 쿼리 시간 측정 시작

		Sort sortOrder = SortUtil.parseSortParameter(sort);
		Pageable pageable = PageRequest.of(page, size, sortOrder);

		// 1. 가게 정보 조회 (엔티티)
		Page<Store> storePage;
		if (lat != null && lng != null) {
			logger.debug("위치 기반 가게 조회 수행: lat={}, lng={}, radius={}, ...", lat, lng, radius);
			storePage = storeRepository.findStoresWithLocation(
					categoryId, storeName, isDiscountOpen, lat, lng, radius, pageable);
		} else {
			logger.debug("위치 정보 없는 가게 조회 수행: ...");
			storePage = storeRepository.findStoresWithoutLocation(
					categoryId, storeName, isDiscountOpen, pageable);
		}

		List<Store> stores = storePage.getContent();
		List<StoreListDto> content;

		if (!stores.isEmpty()) {
			// 2. 조회된 가게 ID 목록 추출
			List<Long> storeIds = stores.stream().map(Store::getId).collect(Collectors.toList());

			// 3. 리뷰 수 조회
			List<Map<String, Object>> reviewCountsResult = reviewRepository.findReviewCountsByStoreIds(storeIds);
			logger.debug("리뷰 수 조회 결과 ({}개 가게): {}", reviewCountsResult.size(), reviewCountsResult);

			// 4. 리뷰 수 Map 생성 (storeId -> reviewCount)
			Map<Long, Long> reviewCountMap = reviewCountsResult.stream()
					.collect(Collectors.toMap(
						map -> (Long) map.get("storeId"),
						map -> (Long) map.get("reviewCount"),
						(count1, count2) -> count1 // 중복 키 발생 시 처리 (이론상 발생 안 함)
					)); 
			logger.debug("생성된 리뷰 수 Map: {}", reviewCountMap);

			// 5. DTO 변환 및 리뷰 수 설정
			content = stores.stream().map(store -> {
				StoreListDto dto = StoreListDto.fromEntity(store);
				long count = reviewCountMap.getOrDefault(store.getId(), 0L);
				dto.setReviewCount(count);
				logger.trace("Store ID: {}, Review Count: {}", store.getId(), count);
				return dto;
			}).collect(Collectors.toList());
		} else {
			content = Collections.emptyList();
		}

		long totalElements = storePage.getTotalElements();
		int totalPages = storePage.getTotalPages();
		
		long endTime = System.currentTimeMillis(); // 쿼리 시간 측정 종료
		long queryExecutionTimeMs = endTime - startTime;
		logger.debug("가게 조회 쿼리 실행 시간 (리뷰 수 포함): {}ms", queryExecutionTimeMs);

		return new StoreQueryResult(content, totalElements, totalPages, queryExecutionTimeMs);
	}

	/**
	 * 캐시 키 생성 메서드
	 */
	public static String generateCacheKey(String sort, Boolean isDiscountOpen, int page, int size, int categoryId) {
		return String.format("%s_%s_%d_%d_%d",
				sort != null ? sort : "default",
				isDiscountOpen != null ? isDiscountOpen.toString() : "all",
				page, size, categoryId);
	}

	/**
	 * 현재 인증된 사용자의 ID를 반환합니다. (주석 처리됨 - Member 클래스 필요)
	 * Spring Security Context Holder를 사용합니다.
	 */
	private Long getCurrentUserId() {
		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //
		// if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
		// 	throw new StoreUnauthenticatedException("인증되지 않은 사용자입니다.");
		// }
        //
		// // Principal 객체에서 사용자 정보 추출
		// Object principal = authentication.getPrincipal();
		// if (principal instanceof Member) { // Member 클래스 필요
		// 	return ((Member) principal).getId();
		// } else if (principal instanceof String && principal.equals("anonymousUser")) {
		// 	// 이 경우는 위에서 이미 처리되었지만 명확성을 위해 남겨둠
		// 	throw new StoreUnauthenticatedException("익명 사용자는 ID를 가질 수 없습니다.");
		// } else {
		// 	// 다른 Principal 타입 처리 (예: UserDetails 구현체, OAuth2 사용자 정보 등)
		// 	// 프로젝트의 인증 방식에 맞게 캐스팅 및 ID 추출 로직 필요
		// 	logger.warn("알 수 없는 Principal 타입 또는 사용자 정보 구조: {}", principal != null ? principal.getClass().getName() : "null");
		// 	// 예시: UserDetails 인터페이스를 사용하는 경우
		// 	// if (principal instanceof UserDetails) {
		// 	//     // UserDetails 구현체에서 ID를 가져오는 로직 (예: 커스텀 UserDetails 클래스)
		// 	//     // return ((CustomUserDetails) principal).getId();
		// 	// }
		// 	throw new StoreUnauthenticatedException("사용자 ID를 추출할 수 없습니다.");
		// }
		// 임시 반환값 - 실제 구현 필요
		logger.warn("getCurrentUserId() 호출되었으나 실제 로직은 주석 처리됨. 임시로 1L 반환.");
		return 1L;
	}

	/**
	 * 현재 인증된 사용자가 등록한 가게 목록을 조회합니다. (주석 처리됨 - Member 클래스 필요)
	 */
	public MyStoreResponseDto getMyStore() {
		// Long userId = getCurrentUserId(); // Member 관련 로직 필요
		// Member member = memberRepository.findById(userId) // MemberRepository 및 Member 필요
		// 		.orElseThrow(() -> new UserNotFoundException("유저 정보를 찾을 수 없습니다."));
        //
		// // 사용자가 관리하는 활성 가게 목록 조회 (findAllByUserIdAndDeletedAtIsNull 사용 가정)
		// List<Store> stores = storeRepository.findAllByUserIdAndDeletedAtIsNull(userId); // Repository 메서드 확인 필요
        //
		// // MyStoreResponseDto 생성 (멤버 정보와 가게 목록 포함)
		// return new MyStoreResponseDto(member.getId(), member.getName(), member.getEmail(), stores);

		// 임시 반환값 또는 예외 발생 - 실제 구현 필요
		throw new UnsupportedOperationException("getMyStore 기능은 현재 비활성화 상태입니다. (Member 관련 구현 필요)");
	}

}