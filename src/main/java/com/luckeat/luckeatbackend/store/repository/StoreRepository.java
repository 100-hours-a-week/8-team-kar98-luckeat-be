package com.luckeat.luckeatbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.store.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
