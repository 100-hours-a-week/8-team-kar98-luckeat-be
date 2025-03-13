package com.luckeat.luckeatbackend.store.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@GetMapping
	public ResponseEntity<List<Store>> getAllStores() {
		return ResponseEntity.ok(storeService.getAllStores());
	}

	@GetMapping("/{store_id}")
	public ResponseEntity<Store> getStoreById(@PathVariable Long storeId) {
		return storeService.getStoreById(storeId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Store> createStore(@RequestBody Store store) {
		return ResponseEntity.status(HttpStatus.CREATED).body(storeService.saveStore(store));
	}

	@PutMapping("/{store_id}")
	public ResponseEntity<Store> updateStore(@PathVariable Long storeId, @RequestBody Store store) {
		return storeService.getStoreById(storeId).map(existingStore -> {
			store.setId(storeId);
			return ResponseEntity.ok(storeService.saveStore(store));
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{store_id}")
	public ResponseEntity<Void> deleteStore(@PathVariable Long storeId) {
		// TODO: 소프트 삭제로 바꾸기
		return storeService.getStoreById(storeId).map(store -> {
			storeService.deleteStore(storeId);
			return ResponseEntity.noContent().<Void>build();
		}).orElse(ResponseEntity.notFound().build());

	}
}
