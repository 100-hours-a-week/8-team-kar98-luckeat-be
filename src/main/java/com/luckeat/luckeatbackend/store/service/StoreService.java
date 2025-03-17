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
import com.luckeat.luckeatbackend.store.dto.StoreDto;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

	private final StoreRepository storeRepository;
	private final ProductRepository productRepository;

	public List<StoreDto.Response> getAllStores() {
		return storeRepository.findAllByDeletedAtIsNull().stream().map(StoreDto.Response::fromEntity).toList();
	}

	public List<StoreDto.Response> getStoresByCategory(Long categoryId) {
		return storeRepository.findAllByCategoryId(categoryId).stream().filter(store -> store.getDeletedAt() == null)
				.map(StoreDto.Response::fromEntity).toList();
	}

	public List<StoreDto.Response> getStoresByUser(Long userId) {
		return storeRepository.findAllByUserId(userId).stream().filter(store -> store.getDeletedAt() == null)
				.map(StoreDto.Response::fromEntity).toList();
	}

	public StoreDto.Response getStoreById(Long storeId) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));
		return StoreDto.Response.fromEntity(store);
	}

	public StoreDto.DetailResponse getStoreDetailById(Long storeId) {
		Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));

		// 가게와 연관된 모든 제품을 한 번에 조회 (매핑이 설정되어 있어 자동으로 조인)
		List<Product> activeProducts = store.getProducts().stream().filter(product -> product.getDeletedAt() == null)
				.collect(Collectors.toList());

		return StoreDto.DetailResponse.fromEntity(store, activeProducts);
	}

	@Transactional
	public StoreDto.Response createStore(StoreDto.Request request) {
		// 현재 인증된 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(authentication.getName());

		Store store = request.toEntity(userId);
		Store savedStore = storeRepository.save(store);
		return StoreDto.Response.fromEntity(savedStore);
	}

	@Transactional
	public StoreDto.Response updateStore(Long storeId, StoreDto.Request request) {
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

		return StoreDto.Response.fromEntity(storeRepository.save(updatedStore));
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

	public List<StoreDto.Response> getStores(Long categoryId, Long userId, Double lat, Double lng, Double radius,
			String sort) {
		List<Store> stores;

		// 필터링 로직
		if (categoryId != null) {
			stores = storeRepository.findAllByCategoryId(categoryId).stream()
					.filter(store -> store.getDeletedAt() == null).collect(Collectors.toList());
		} else if (userId != null) {
			stores = storeRepository.findAllByUserId(userId).stream().filter(store -> store.getDeletedAt() == null)
					.collect(Collectors.toList());
		} else {
			stores = storeRepository.findAllByDeletedAtIsNull();
		}

		// 위치 기반 필터링
		if (lat != null && lng != null && radius != null) {
			stores = filterByDistance(stores, lat, lng, radius);
		}

		// 정렬 로직
		if (sort != null) {
			sortStores(stores, sort, lat, lng);
		}

		return stores.stream().map(StoreDto.Response::fromEntity).collect(Collectors.toList());
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
