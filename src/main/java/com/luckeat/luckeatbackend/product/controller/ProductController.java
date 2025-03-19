package com.luckeat.luckeatbackend.product.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.common.exception.product.ProductNotFoundException;
import com.luckeat.luckeatbackend.product.dto.ProductRequestDto;
import com.luckeat.luckeatbackend.product.dto.ProductResponseDto;
import com.luckeat.luckeatbackend.product.dto.ProductStatusRequestDto;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores/{store_id}/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	// 가게의 상품 보기
	@GetMapping
	public ResponseEntity<List<ProductResponseDto>> getAllProducts(@PathVariable("store_id") Long storeId) {
		List<ProductResponseDto> productResponses = productService.getAllProducts(storeId).stream()
				.map(ProductResponseDto::fromEntity)
				.collect(Collectors.toList());
		return ResponseEntity.ok(productResponses);
	}

	// 상품 ID로 조회
	@GetMapping("/{product_id}")
	public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("store_id") Long storeId, @PathVariable("product_id") Long productId) {
		Product product = productService.getProductById(storeId, productId)
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		return ResponseEntity.ok(ProductResponseDto.fromEntity(product));
	}

	// 상품 등록
	@PostMapping
	public ResponseEntity<ProductResponseDto> createProduct(@PathVariable("store_id") Long storeId, @Valid @RequestBody ProductRequestDto productRequestDto) {
		Product product = productRequestDto.toEntity();
		Product savedProduct = productService.saveProduct(storeId, product);
		return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponseDto.fromEntity(savedProduct));
	}

	// 상품 정보 수정
	@PutMapping("/{product_id}")
	public ResponseEntity<ProductResponseDto> updateProduct(
			@PathVariable("store_id") Long storeId, 
			@PathVariable("product_id") Long productId,
			@Valid @RequestBody ProductRequestDto productRequestDto) {
		
		Product existingProduct = productService.getProductById(storeId, productId)
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		
		productRequestDto.updateEntity(existingProduct);
		Product updatedProduct = productService.saveProduct(storeId, existingProduct);
		return ResponseEntity.ok(ProductResponseDto.fromEntity(updatedProduct));
	}

	// 상품 상태(open) 수정
	@PatchMapping("/{product_id}/status")
	public ResponseEntity<ProductResponseDto> updateProductStatus(
			@PathVariable("store_id") Long storeId, 
			@PathVariable("product_id") Long productId,
			@Valid @RequestBody ProductStatusRequestDto statusRequestDto) {
		
		Product product = productService.updateProductStatus(storeId, productId, statusRequestDto.getIsOpen())
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		
		return ResponseEntity.ok(ProductResponseDto.fromEntity(product));
	}

	// 상품 삭제
	@DeleteMapping("/{product_id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable("store_id") Long storeId, @PathVariable("product_id") Long productId) {
		// 상품이 존재하는지 먼저 확인
		productService.getProductById(storeId, productId)
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		
		productService.deleteProduct(storeId, productId);
		return ResponseEntity.noContent().build();
	}
}
