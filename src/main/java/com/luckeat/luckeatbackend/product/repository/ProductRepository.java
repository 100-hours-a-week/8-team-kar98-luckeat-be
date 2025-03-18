package com.luckeat.luckeatbackend.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.store.model.Store;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByStore(Store store);

	List<Product> findByStoreAndIsOpenTrue(Store store);

	List<Product> findByStoreAndDeletedAtIsNull(Store store);

	List<Product> findByStoreAndIsOpenTrueAndDeletedAtIsNull(Store store);

	// 특정 가게의 마감할인 중인 상품 개수 조회
	// 삭제되지 않은 상품 중에서 마감할인 중인 상품 개수 조회
	// 1개 이상이면 마감할인 중인 가게임
	long countByStoreIdAndIsOpenTrueAndDeletedAtIsNull(Long storeId);
}
