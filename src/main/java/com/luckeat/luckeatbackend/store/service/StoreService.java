package com.luckeat.luckeatbackend.store.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

	private final StoreRepository storeRepository;

	public List<Store> getAllStores() {
		return storeRepository.findAll();
	}

	public Optional<Store> getStoreById(Long id) {
		return storeRepository.findById(id);
	}

	@Transactional
	public Store saveStore(Store store) {
		return storeRepository.save(store);
	}

	@Transactional
	public void deleteStore(Long storeId) {
		// TODO: 소프트 삭제로 바꾸기
		storeRepository.deleteById(storeId);
	}
}
