package com.luckeat.luckeatbackend.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.store.model.Store;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByStore(Store store);

	List<Product> findByStoreAndProductCountGreaterThan(Store store, Long count);

	List<Product> findByStoreAndDeletedAtIsNull(Store store);

	List<Product> findByStoreAndProductCountGreaterThanAndDeletedAtIsNull(Store store, Long count);

	Optional<Product> findByIdAndStore(Long id, Store store);

	Optional<Product> findByIdAndStoreAndDeletedAtIsNull(Long id, Store store);

	Optional<Product> findByIdAndStoreAndProductCountGreaterThan(Long id, Store store, Long count);
	// 특정 가게의 마감할인 중인 상품 개수 조회
	// 삭제되지 않은 상품 중에서 마감할인 중인 상품 개수 조회
	// 1개 이상이면 마감할인 중인 가게임
	long countByStoreIdAndProductCountGreaterThanAndDeletedAtIsNull(Long storeId, Long count);

    boolean existsByStoreIdAndIsOpenTrueAndDeletedAtIsNull(Long storeId);

    boolean existsByStoreIdAndDeletedAtIsNull(Long storeId);
}
