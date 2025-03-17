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
}
