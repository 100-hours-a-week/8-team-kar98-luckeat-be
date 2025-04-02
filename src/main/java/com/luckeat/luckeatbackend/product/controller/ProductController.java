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
import com.luckeat.luckeatbackend.product.dto.ProductCountRequestDto;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/stores/{store_id}/products")
@RequiredArgsConstructor
@Tag(name = "상품 API", description = "상품 관련 API 목록")
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "가게의 상품 목록 조회", description = "특정 가게의 모든 상품 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
	})
	@GetMapping
	public ResponseEntity<List<ProductResponseDto>> getAllProducts(
			@Parameter(description = "가게 ID", required = true)
			@PathVariable("store_id") Long storeId) {
		List<ProductResponseDto> productResponses = productService.getAllProducts(storeId).stream()
				.map(ProductResponseDto::fromEntity)
				.collect(Collectors.toList());
		return ResponseEntity.ok(productResponses);
	}

	@Operation(summary = "상품 조회", description = "상품 ID로 특정 상품을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 조회 성공"),
		@ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
	})
	@GetMapping("/{product_id}")
	public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("store_id") Long storeId, @PathVariable("product_id") Long productId) {
		Product product = productService.getProductById(storeId, productId)
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		return ResponseEntity.ok(ProductResponseDto.fromEntity(product));
	}

	@Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "상품 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
	})
	@PostMapping
	public ResponseEntity<ProductResponseDto> createProduct(
			@Parameter(description = "가게 ID", required = true)
			@PathVariable("store_id") Long storeId,
			@Parameter(description = "상품 정보", required = true)
			@Valid @RequestBody ProductRequestDto productRequestDto) {
		Product product = productRequestDto.toEntity();
		Product savedProduct = productService.saveProduct(storeId, product);
		return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponseDto.fromEntity(savedProduct));
	}

	@Operation(summary = "상품 정보 수정", description = "기존 상품의 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 정보 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
		@ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
		@ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
	})
	@PutMapping("/{product_id}")
	public ResponseEntity<ProductResponseDto> updateProduct(
			@Parameter(description = "가게 ID", required = true)
			@PathVariable("store_id") Long storeId,
			@Parameter(description = "상품 ID", required = true)
			@PathVariable("product_id") Long productId,
			@Parameter(description = "수정할 상품 정보", required = true)
			@Valid @RequestBody ProductRequestDto productRequestDto) {
		
		Product existingProduct = productService.getProductById(storeId, productId)
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		
		productRequestDto.updateEntity(existingProduct);
		Product updatedProduct = productService.saveProduct(storeId, existingProduct);
		return ResponseEntity.ok(ProductResponseDto.fromEntity(updatedProduct));
	}

	@Operation(summary = "상품 수량 수정", description = "상품의 수량을 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 수량 수정 성공"),
		@ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
	})
	@PatchMapping("/{product_id}/count")
	public ResponseEntity<ProductResponseDto> updateProductCount(
			@PathVariable("store_id") Long storeId, 
			@PathVariable("product_id") Long productId,
			@Valid @RequestBody ProductCountRequestDto countRequestDto) {
		
		Product product = productService.updateProductCount(storeId, productId, countRequestDto.getProductCount())
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		
		return ResponseEntity.ok(ProductResponseDto.fromEntity(product));
	}

	@Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "상품 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
	})
	@DeleteMapping("/{product_id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable("store_id") Long storeId, @PathVariable("product_id") Long productId) {
		// 상품이 존재하는지 먼저 확인
		productService.getProductById(storeId, productId)
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		
		productService.deleteProduct(storeId, productId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "상품 판매 상태 수정", description = "상품의 판매 상태(오픈/마감)를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 판매 상태 수정 성공"),
		@ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음", content = @Content)
	})
	@PatchMapping("/{product_id}/status")
	public ResponseEntity<ProductResponseDto> updateProductStatus(
			@PathVariable("store_id") Long storeId, 
			@PathVariable("product_id") Long productId,
			@Valid @RequestBody ProductStatusRequestDto statusRequestDto) {
		
		Product product = productService.updateProductStatus(storeId, productId, statusRequestDto.getIsOpen())
				.orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
		
		return ResponseEntity.ok(ProductResponseDto.fromEntity(product));
	}
}
