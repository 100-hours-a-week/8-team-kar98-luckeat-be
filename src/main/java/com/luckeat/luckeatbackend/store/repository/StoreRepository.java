package com.luckeat.luckeatbackend.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.store.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

	List<Store> findAllByUserId(Long userId);

	List<Store> findAllByDeletedAtIsNull();

	Page<Store> findAllByDeletedAtIsNull(Pageable pageable);

	Optional<Store> findByIdAndDeletedAtIsNull(Long id);

	List<Store> findByStoreNameContainingAndDeletedAtIsNull(String storeName);

	Optional<Store> findByUserIdAndDeletedAtIsNull(Long userId);

	Optional<Store> findByStoreUrl(String storeUrl);

	List<Store> findAllByCategoryId(Long categoryId);

	// 위치 기반 필터링 (정렬은 Pageable에 위임, 거리순 정렬은 특수 처리 필요)
	@Query(value = "SELECT *, " +
		   "(6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(s.latitude)))) as distance " +
		   "FROM store s WHERE s.deleted_at IS NULL " +
		   "AND (:categoryId = 0 OR s.category_id = :categoryId) " +
		   "AND (:storeName IS NULL OR LOWER(s.store_name) LIKE LOWER(CONCAT('%', :storeName, '%'))) " +
		   "AND (:isDiscountOpen IS NULL OR :isDiscountOpen = " +
		   "EXISTS (SELECT 1 FROM product p WHERE p.store_id = s.id AND p.is_open = true AND p.deleted_at IS NULL)) " +
		   "AND (:radius IS NULL OR " +
		   "(6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(s.latitude)))) <= :radius) " +
		   "ORDER BY distance ASC", // 네이티브 쿼리 내 정렬 로직은 유지 (거리순)
		   countQuery = "SELECT COUNT(*) FROM store s WHERE s.deleted_at IS NULL " +
		   "AND (:categoryId = 0 OR s.category_id = :categoryId) " +
		   "AND (:storeName IS NULL OR LOWER(s.store_name) LIKE LOWER(CONCAT('%', :storeName, '%'))) " +
		   "AND (:isDiscountOpen IS NULL OR :isDiscountOpen = " +
		   "EXISTS (SELECT 1 FROM product p WHERE p.store_id = s.id AND p.is_open = true AND p.deleted_at IS NULL)) " +
		   "AND (:radius IS NULL OR " +
		   "(6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(s.latitude)))) <= :radius)",
		   nativeQuery = true)
	Page<Store> findStoresWithLocation( // 메소드 시그니처에서 sort 파라미터 제거
			@Param("categoryId") int categoryId,
			@Param("storeName") String storeName,
			@Param("isDiscountOpen") Boolean isDiscountOpen,
			@Param("lat") Double lat,
			@Param("lng") Double lng,
			@Param("radius") Double radius,
			Pageable pageable);

	// 위치 없이 정렬만 하는 쿼리 (정렬은 Pageable에 위임)
	@Query(value = "SELECT * FROM store s WHERE s.deleted_at IS NULL " +
		   "AND (:categoryId = 0 OR s.category_id = :categoryId) " +
		   "AND (:storeName IS NULL OR LOWER(s.store_name) LIKE LOWER(CONCAT('%', :storeName, '%'))) " +
		   "AND (:isDiscountOpen IS NULL OR :isDiscountOpen = " +
		   "EXISTS (SELECT 1 FROM product p WHERE p.store_id = s.id AND p.is_open = true AND p.deleted_at IS NULL))", // 네이티브 쿼리 내 ORDER BY 제거
		   countQuery = "SELECT COUNT(*) FROM store s WHERE s.deleted_at IS NULL " +
		   "AND (:categoryId = 0 OR s.category_id = :categoryId) " +
		   "AND (:storeName IS NULL OR LOWER(s.store_name) LIKE LOWER(CONCAT('%', :storeName, '%'))) " +
		   "AND (:isDiscountOpen IS NULL OR :isDiscountOpen = " +
		   "EXISTS (SELECT 1 FROM product p WHERE p.store_id = s.id AND p.is_open = true AND p.deleted_at IS NULL))",
		   nativeQuery = true)
	Page<Store> findStoresWithoutLocation( // 메소드 시그니처에서 sort 파라미터 제거
			@Param("categoryId") int categoryId,
			@Param("storeName") String storeName,
			@Param("isDiscountOpen") Boolean isDiscountOpen,
			Pageable pageable);
}
