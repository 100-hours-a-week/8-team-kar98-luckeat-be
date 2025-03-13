package com.luckeat.luckeatbackend.product.controller;

import java.time.LocalDateTime;
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

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores/{store_id}/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts(@PathVariable Long storeId) {
		return ResponseEntity.ok(productService.getAllProducts(storeId));
	}

	@GetMapping("/{product_id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long storeId, @PathVariable Long productId) {
		return productService.getProductById(storeId, productId).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Product> createProduct(@PathVariable Long storeId, @RequestBody Product product) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(storeId, product));
	}

	@PutMapping("/{product_id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long storeId, @PathVariable Long productId,
			@RequestBody Product product) {
		return productService.getProductById(storeId, productId).map(existingProduct -> {
			product.setId(productId);
			return ResponseEntity.ok(productService.saveProduct(storeId, product));
		}).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{product_id}/status")
	public ResponseEntity<Product> updateProductStatus(@PathVariable Long storeId, @PathVariable Long productId,
			@RequestBody boolean isOpen) {
		return productService.updateProductStatus(productId, isOpen).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{product_id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long storeId, @PathVariable Long productId) {
		return productService.getProductById(storeId, productId).map(product -> {
			product.setDeletedAt(LocalDateTime.now());
			productService.saveProduct(storeId, product);
			return ResponseEntity.noContent().<Void>build();
		}).orElse(ResponseEntity.notFound().build());
	}
}
