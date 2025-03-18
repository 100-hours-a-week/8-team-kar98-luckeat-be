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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.store.dto.StoreDetailResponseDto;
import com.luckeat.luckeatbackend.store.dto.StoreRequestDto;
import com.luckeat.luckeatbackend.store.dto.StoreResponseDto;
import com.luckeat.luckeatbackend.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@GetMapping
	public ResponseEntity<List<StoreResponseDto>> getAllStores(@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Double lat, @RequestParam(required = false) Double lng,
			@RequestParam(required = false) Double radius, @RequestParam(required = false) String sort,
			@RequestParam(required = false) String storeName, @RequestParam(required = false) Boolean isDiscountOpen) {

		return ResponseEntity.ok(storeService.getStores(categoryId, lat, lng, radius, sort, storeName, isDiscountOpen));
	}

	@GetMapping("/{store_id}")
	public ResponseEntity<StoreDetailResponseDto> getStoreDetailById(@PathVariable("store_id") Long storeId) {
		return ResponseEntity.ok(storeService.getStoreDetailById(storeId));
	}

	@PostMapping
	public ResponseEntity<StoreResponseDto> createStore(@RequestBody StoreRequestDto storeRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(storeService.createStore(storeRequest));
	}

	@PutMapping("/{store_id}")
	public ResponseEntity<StoreResponseDto> updateStore(@PathVariable("store_id") Long storeId,
			@RequestBody StoreRequestDto storeRequest) {
		return ResponseEntity.ok(storeService.updateStore(storeId, storeRequest));
	}

	@DeleteMapping("/{store_id}")
	public ResponseEntity<Void> deleteStore(@PathVariable("store_id") Long storeId) {
		storeService.deleteStore(storeId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{store_id}/share")
	public ResponseEntity<Void> incrementShareCount(@PathVariable("store_id") Long storeId) {
		storeService.incrementShareCount(storeId);
		return ResponseEntity.ok().build();
	}
}
