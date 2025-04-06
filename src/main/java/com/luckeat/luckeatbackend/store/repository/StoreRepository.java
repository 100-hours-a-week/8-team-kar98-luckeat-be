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

	// 위치 기반 필터링 및 정렬을 위한 네이티브 쿼리
    @Query(value = "SELECT * FROM store s WHERE s.deleted_at IS NULL " +
           "AND (:categoryId = 0 OR s.category_id = :categoryId) " +
           "AND (:storeName IS NULL OR LOWER(s.store_name) LIKE LOWER(CONCAT('%', :storeName, '%'))) " +
           "AND (:isDiscountOpen IS NULL OR :isDiscountOpen = " +
           "EXISTS (SELECT 1 FROM product p WHERE p.store_id = s.id AND p.is_open = true AND p.deleted_at IS NULL)) " +
           "AND (:radius IS NULL OR " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(s.latitude)))) <= :radius) " +
           "ORDER BY " +
           "CASE WHEN :sort = 'distance' THEN (6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(s.latitude)))) END ASC, " +
           "CASE WHEN :sort = 'share' THEN s.share_count END DESC, " +
           "CASE WHEN :sort = 'rating' THEN s.avg_rating_google END DESC",
           countQuery = "SELECT COUNT(*) FROM store s WHERE s.deleted_at IS NULL " +
           "AND (:categoryId = 0 OR s.category_id = :categoryId) " +
           "AND (:storeName IS NULL OR LOWER(s.store_name) LIKE LOWER(CONCAT('%', :storeName, '%'))) " +
           "AND (:isDiscountOpen IS NULL OR :isDiscountOpen = " +
           "EXISTS (SELECT 1 FROM product p WHERE p.store_id = s.id AND p.is_open = true AND p.deleted_at IS NULL)) " +
           "AND (:radius IS NULL OR " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(s.latitude)))) <= :radius)",
           nativeQuery = true)
    Page<Store> findStoresWithLocation(
            @Param("categoryId") int categoryId,
            @Param("storeName") String storeName,
            @Param("isDiscountOpen") Boolean isDiscountOpen,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius,
            @Param("sort") String sort,
            Pageable pageable);

	// 위치 없이 정렬만 하는 쿼리
       @Query(value = "SELECT * FROM store s WHERE s.deleted_at IS NULL " +
              "AND (:categoryId = 0 OR s.category_id = :categoryId) " +
              "AND (:storeName IS NULL OR LOWER(s.store_name) LIKE LOWER(CONCAT('%', :storeName, '%'))) " +
              "AND (:isDiscountOpen IS NULL OR :isDiscountOpen = " +
              "EXISTS (SELECT 1 FROM product p WHERE p.store_id = s.id AND p.is_open = true AND p.deleted_at IS NULL)) " +
              "ORDER BY " +
              "CASE WHEN :sort = 'rating' THEN s.avg_rating_google ELSE NULL END DESC, " +
              "CASE WHEN :sort = 'share' THEN s.share_count ELSE NULL END DESC",
              countQuery = "SELECT COUNT(*) FROM store s WHERE s.deleted_at IS NULL " +
              "AND (:categoryId = 0 OR s.category_id = :categoryId) " +
              "AND (:storeName IS NULL OR LOWER(s.store_name) LIKE LOWER(CONCAT('%', :storeName, '%'))) " +
              "AND (:isDiscountOpen IS NULL OR :isDiscountOpen = " +
              "EXISTS (SELECT 1 FROM product p WHERE p.store_id = s.id AND p.is_open = true AND p.deleted_at IS NULL))",
              nativeQuery = true)
    Page<Store> findStoresWithoutLocation(
            @Param("categoryId") int categoryId,
            @Param("storeName") String storeName,
            @Param("isDiscountOpen") Boolean isDiscountOpen,
            @Param("sort") String sort,
            Pageable pageable);
}
