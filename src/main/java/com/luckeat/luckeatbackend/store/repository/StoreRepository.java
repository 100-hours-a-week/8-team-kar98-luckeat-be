package com.luckeat.luckeatbackend.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.store.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

	List<Store> findAllByUserId(Long userId);

	List<Store> findAllByDeletedAtIsNull();

	Optional<Store> findByIdAndDeletedAtIsNull(Long id);

	List<Store> findByStoreNameContainingAndDeletedAtIsNull(String storeName);

	Optional<Store> findByUserIdAndDeletedAtIsNull(Long userId);

	Optional<Store> findByStoreUrl(String storeUrl);

	List<Store> findAllByCategoryId(Long categoryId);
}
