package com.luckeat.luckeatbackend.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	private final ProductRepository productRepository;

	public List<Product> getAllProducts(Long storeId) {
		return productRepository.findByStoreId(storeId);
	}

	public Optional<Product> getProductById(Long storeId, Long productId) {
		return productRepository.findById(productId);
	}

	public List<Product> getOpenProductsByStoreId(Long storeId) {
		return productRepository.findByStoreIdAndIsOpenTrue(storeId);
	}

	public Optional<Product> getProductById(Long id) {
		return productRepository.findById(id);
	}

	@Transactional
	public Product saveProduct(Long storeId, Product product) {
		if (product.getDiscountedPrice() == null) {
			product.setDiscountedPrice(product.getOriginalPrice());
		}
		product.setStoreId(storeId);
		return productRepository.save(product);
	}

	@Transactional
	public void deleteProduct(Long storeId, Long productId) {
		productRepository.deleteById(productId);
	}

	@Transactional
	public Optional<Product> updateProductStatus(Long productId, boolean isOpen) {
		return productRepository.findById(productId).map(product -> {
			product.setIsOpen(isOpen);
			return productRepository.save(product);
		});
	}
}
