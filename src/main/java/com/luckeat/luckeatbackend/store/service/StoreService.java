package com.luckeat.luckeatbackend.store.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.common.exception.store.StoreForbiddenException;
import com.luckeat.luckeatbackend.common.exception.store.StoreNotFoundException;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.repository.ProductRepository;
import com.luckeat.luckeatbackend.store.dto.StoreDetailResponseDto;
import com.luckeat.luckeatbackend.store.dto.StoreRequestDto;
import com.luckeat.luckeatbackend.store.dto.StoreResponseDto;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;

	public List<StoreResponseDto> getAllStores() {
		return storeRepository.findAllByDeletedAtIsNull().stream().map(StoreResponseDto::fromEntity).toList();
	}

	public List<StoreResponseDto> getStoresByCategory(Long categoryId) {
		return storeRepository.findAllByCategoryId(categoryId).stream().filter(store -> store.getDeletedAt() == null)
				.map(StoreResponseDto::fromEntity).toList();
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

		return StoreDetailResponseDto.fromEntity(store, activeProducts);
	}

	@Transactional
	public StoreResponseDto createStore(StoreRequestDto request) {
		// 현재 인증된 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(authentication.getName());

		Store store = request.toEntity(userId);
		Store savedStore = storeRepository.save(store);
		return StoreResponseDto.fromEntity(savedStore);
	}

	@Transactional
	public StoreResponseDto updateStore(Long storeId, StoreRequestDto request) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));

		// 권한 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(authentication.getName());

		if (!store.getUserId().equals(userId)) {
			throw new StoreForbiddenException("해당 가게에 대한 수정 권한이 없습니다.");
		}

		// 기존 ID와 사용자 ID 유지
		Store updatedStore = request.toEntity(userId);
		updatedStore.setId(storeId);
		updatedStore.setShareCount(store.getShareCount()); // 공유 카운트 유지

		return StoreResponseDto.fromEntity(storeRepository.save(updatedStore));
	}

	@Transactional
	public void deleteStore(Long storeId) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));

		// 권한 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(authentication.getName());

		if (!store.getUserId().equals(userId)) {
			throw new StoreForbiddenException("해당 가게에 대한 삭제 권한이 없습니다.");
		}

		// 논리적 삭제를 위해 deletedAt 필드 업데이트
		store.setDeletedAt(java.time.LocalDateTime.now());
		storeRepository.save(store);
	}

	@Transactional
	public void incrementShareCount(Long storeId) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));

		store.setShareCount(store.getShareCount() + 1);
		storeRepository.save(store);
	}

	/**
	 * 다양한 필터 조건을 기반으로 가게 목록을 검색하는 메서드
	 * 
	 * @param categoryId
	 *            카테고리 ID (선택적)
	 * @param lat
	 *            위도 (선택적)
	 * @param lng
	 *            경도 (선택적)
	 * @param radius
	 *            검색 반경 (km) (선택적)
	 * @param sort
	 *            정렬 기준 (distance: 거리순, share: 공유순) (선택적)
	 * @param storeName
	 *            가게 이름 검색어 (선택적)
	 * @param isDiscountOpen
	 *            마감 할인 중인 가게만 필터링 (선택적)
	 * @return 필터링 및 정렬된 가게 목록 DTO
	 */
	public List<StoreResponseDto> getStores(Long categoryId, Double lat, Double lng, Double radius, String sort,
			String storeName, Boolean isDiscountOpen) {
		// 1. 기본적으로 삭제되지 않은 모든 가게 조회 (deletedAt이 null인 가게들)
		List<Store> stores = storeRepository.findAllByDeletedAtIsNull();

		// 2. 가게명 검색 필터링 (storeName 파라미터가 존재하고 비어있지 않은 경우)
		// - 대소문자 구분 없이 가게 이름에 검색어가 포함된 가게들만 필터링
		if (storeName != null && !storeName.trim().isEmpty()) {
			String searchTerm = storeName.trim().toLowerCase();
			stores = stores.stream().filter(store -> store.getStoreName().toLowerCase().contains(searchTerm))
					.collect(Collectors.toList());
		}

		// 3. 카테고리 필터링 (categoryId 파라미터가 존재하는 경우)
		// - 해당 카테고리에 속한 가게들만 필터링
		if (categoryId != null) {
			stores = stores.stream().filter(store -> store.getCategoryId().equals(categoryId))
					.collect(Collectors.toList());
		}

		// 4. 위치 기반 필터링 (위도, 경도, 반경이 모두 제공된 경우)
		// - 사용자 위치로부터 지정된 반경 내에 있는 가게들만 필터링
		// - Haversine 공식을 사용하여 두 지점 간의 거리 계산
		if (lat != null && lng != null && radius != null) {
			stores = filterByDistance(stores, lat, lng, radius);
		}

		// 5. 마감할인 필터링
		// - isDiscountOpen=true인 경우: 마감할인 중인 가게만 표시
		// - isDiscountOpen=false인 경우: 마감할인 중이 아닌 가게만 표시
		if (isDiscountOpen != null) {
			stores = stores.stream().filter(store -> {
				// 가게의 상품들 중 is_open이 true이고 삭제되지 않은 상품 개수 확인
				long openProductCount = productRepository.countByStoreIdAndIsOpenTrueAndDeletedAtIsNull(store.getId());
				// isDiscountOpen이 true면 마감할인 중인 가게만, false면 마감할인 중이 아닌 가게만 반환
				return isDiscountOpen ? openProductCount > 0 : openProductCount == 0;
			}).collect(Collectors.toList());
		}

		// 6. 정렬 로직 적용 (sort 파라미터가 제공된 경우)
		// - distance: 사용자 위치로부터 거리순 정렬
		// - share: 공유 횟수 내림차순 정렬
		if (sort != null) {
			sortStores(stores, sort, lat, lng);
		}

		return stores.stream().map(StoreResponseDto::fromEntity).collect(Collectors.toList());
	}

	// 거리 계산 및 필터링 메소드
	private List<Store> filterByDistance(List<Store> stores, Double lat, Double lng, Double radius) {
		return stores.stream()
				.filter(store -> calculateDistance(lat, lng, store.getLatitude(), store.getLongitude()) <= radius)
				.collect(Collectors.toList());
	}

	// 거리 계산 메소드 (Haversine 공식)
	private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371; // 지구 반경 (km)
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return earthRadius * c;
	}

	// 정렬 메소드
	private void sortStores(List<Store> stores, String sort, Double lat, Double lng) {
		switch (sort) {
			case "distance" :
				if (lat != null && lng != null) {
					stores.sort(Comparator.comparingDouble(
							store -> calculateDistance(lat, lng, store.getLatitude(), store.getLongitude())));
				}
				break;
			case "share" :
				stores.sort(Comparator.comparing(Store::getShareCount).reversed());
				break;
			default :
				// 기본 정렬 또는 다른 정렬 옵션
				break;
		}
	}
}
