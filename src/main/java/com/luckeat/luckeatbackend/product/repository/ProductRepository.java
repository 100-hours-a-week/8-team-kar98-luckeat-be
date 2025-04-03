package com.luckeat.luckeatbackend.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.store.model.Store;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByStore(Store store);

	List<Product> findByStoreAndProductCountGreaterThan(Store store, Long count);

	List<Product> findByStoreAndDeletedAtIsNull(Store store);

	List<Product> findByStoreAndProductCountGreaterThanAndDeletedAtIsNull(Store store, Long count);

	Optional<Product> findByIdAndStore(Long id, Store store);

	Optional<Product> findByIdAndStoreAndDeletedAtIsNull(Long id, Store store);

	Optional<Product> findByIdAndDeletedAtIsNull(Long id);

	Optional<Product> findByIdAndStoreAndProductCountGreaterThan(Long id, Store store, Long count);
	// 특정 가게의 마감할인 중인 상품 개수 조회
	// 삭제되지 않은 상품 중에서 마감할인 중인 상품 개수 조회
	// 1개 이상이면 마감할인 중인 가게임
	long countByStoreIdAndProductCountGreaterThanAndDeletedAtIsNull(Long storeId, Long count);

    boolean existsByStoreIdAndIsOpenTrueAndDeletedAtIsNull(Long storeId);

    boolean existsByStoreIdAndDeletedAtIsNull(Long storeId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(@Param("id") Long id);

	@Modifying
	@Query("UPDATE Product p SET " +
		"p.productCount = CASE WHEN p.productCount >= :quantity THEN p.productCount - :quantity ELSE p.productCount END, " +
		"p.isOpen = CASE WHEN p.productCount >= :quantity AND (p.productCount - :quantity) = 0 THEN false ELSE p.isOpen END " +
		"WHERE p.id = :productId AND p.productCount >= :quantity")
	int decreaseProductStock(@Param("productId") Long productId, @Param("quantity") int quantity);
	
	@Modifying
	@Query("UPDATE Product p SET p.productCount = p.productCount + :quantity, " +
		"p.isOpen = CASE WHEN p.productCount + :quantity > 0 THEN true ELSE p.isOpen END " +
		"WHERE p.id = :productId")
	int increaseProductStock(@Param("productId") Long productId, @Param("quantity") int quantity);

}
