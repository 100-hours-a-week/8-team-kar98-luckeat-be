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

import com.luckeat.luckeatbackend.store.dto.StoreDto;
import com.luckeat.luckeatbackend.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@GetMapping
	public ResponseEntity<List<StoreDto.Response>> getAllStores(
			// 카테고리, 유저, 가까운순 정렬
			@RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long userId,
			@RequestParam(required = false) Double lat, @RequestParam(required = false) Double lng,
			@RequestParam(required = false) Double radius, @RequestParam(required = false) String sort) {
		return ResponseEntity.ok(storeService.getStores(categoryId, userId, lat, lng, radius, sort));
	}

	@GetMapping("/{store_id}")
	public ResponseEntity<StoreDto.Response> getStoreById(@PathVariable("storeId") Long storeId) {
		return ResponseEntity.ok(storeService.getStoreById(storeId));
	}

	@GetMapping("/{store_id}/detail")
	public ResponseEntity<StoreDto.DetailResponse> getStoreDetailById(@PathVariable("storeId") Long storeId) {
		return ResponseEntity.ok(storeService.getStoreDetailById(storeId));
	}

	@PostMapping
	public ResponseEntity<StoreDto.Response> createStore(@RequestBody StoreDto.Request storeRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(storeService.createStore(storeRequest));
	}

	@PutMapping("/{store_id}")
	public ResponseEntity<StoreDto.Response> updateStore(@PathVariable("storeId") Long storeId,
			@RequestBody StoreDto.Request storeRequest) {
		return ResponseEntity.ok(storeService.updateStore(storeId, storeRequest));
	}

	@DeleteMapping("/{store_id}")
	public ResponseEntity<Void> deleteStore(@PathVariable("storeId") Long storeId) {
		storeService.deleteStore(storeId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{store_id}/share")
	public ResponseEntity<Void> incrementShareCount(@PathVariable("storeId") Long storeId) {
		storeService.incrementShareCount(storeId);
		return ResponseEntity.ok().build();
	}
}
