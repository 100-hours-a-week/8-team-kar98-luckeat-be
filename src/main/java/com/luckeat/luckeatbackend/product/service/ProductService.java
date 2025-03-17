package com.luckeat.luckeatbackend.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.common.exception.store.StoreNotFoundException;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.repository.ProductRepository;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	private final ProductRepository productRepository;
	private final StoreRepository storeRepository;

	public List<Product> getAllProducts(Long storeId) {
		Store store = getStoreById(storeId);
		return productRepository.findByStore(store);
	}

	public Optional<Product> getProductById(Long storeId, Long productId) {
		return productRepository.findById(productId);
	}

	public List<Product> getOpenProductsByStoreId(Long storeId) {
		Store store = getStoreById(storeId);
		return productRepository.findByStoreAndIsOpenTrue(store);
	}

	public Optional<Product> getProductById(Long id) {
		return productRepository.findById(id);
	}

	@Transactional
	public Product saveProduct(Long storeId, Product product) {
		if (product.getDiscountedPrice() == null) {
			product.setDiscountedPrice(product.getOriginalPrice());
		}
		Store store = getStoreById(storeId);
		product.setStore(store);
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

	private Store getStoreById(Long storeId) {
		return storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));
	}
}
