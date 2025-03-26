package com.luckeat.luckeatbackend.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.common.exception.product.ProductForbiddenException;
import com.luckeat.luckeatbackend.common.exception.product.ProductInvalidPriceException;
import com.luckeat.luckeatbackend.common.exception.product.ProductNotFoundException;
import com.luckeat.luckeatbackend.common.exception.product.ProductUnauthenticatedException;
import com.luckeat.luckeatbackend.common.exception.store.StoreNotFoundException;
import com.luckeat.luckeatbackend.common.exception.user.UserNotFoundException;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.repository.ProductRepository;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;
import com.luckeat.luckeatbackend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

	private final ProductRepository productRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	public List<Product> getAllProducts(Long storeId) {
		Store store = getStoreById(storeId);
		return productRepository.findByStore(store);
	}

	public Optional<Product> getProductById(Long storeId, Long productId) {
		Store store = getStoreById(storeId);
		return productRepository.findByIdAndStoreAndDeletedAtIsNull(productId, store);
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
		// 권한 검증
		validateStoreOwner(storeId);
		
		Store store = getStoreById(storeId);
		
		// 상품 가격 유효성 검사
		validateProductPrice(product.getOriginalPrice(), product.getDiscountedPrice());
		
		if (product.getDiscountedPrice() == null) {
			product.setDiscountedPrice(product.getOriginalPrice());
		}
		
		product.setStore(store);
		return productRepository.save(product);
	}

	@Transactional
	public void deleteProduct(Long storeId, Long productId) {
		// 권한 검증
		validateStoreOwner(storeId);
		
		// 해당 가게의 상품인지 확인
		Store store = getStoreById(storeId);
		Optional<Product> product = productRepository.findByIdAndStore(productId, store);
		if (product.isPresent()) {
			productRepository.deleteById(productId);
		} else {
			throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId);
		}
	}

	@Transactional
	public Optional<Product> updateProductStatus(Long storeId, Long productId, boolean isOpen) {
		// 권한 검증
		validateStoreOwner(storeId);
		
		// 해당 가게의 상품인지 확인
		Store store = getStoreById(storeId);
		return productRepository.findByIdAndStore(productId, store).map(product -> {
			product.setIsOpen(isOpen);
			return productRepository.save(product);
		});
	}

	private Store getStoreById(Long storeId) {
		return storeRepository.findByIdAndDeletedAtIsNull(storeId)
				.orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));
	}
	
	// JWT에서 현재 사용자 ID 추출
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() 
                || authentication instanceof AnonymousAuthenticationToken) {
            log.error("인증되지 않은 사용자 접근: {}", authentication);
            throw new ProductUnauthenticatedException("인증되지 않은 사용자 접근");
        }

        // 이메일로 사용자 조회하여 ID 반환
        String email = authentication.getName();
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + email)).getId();
    }
    
    // 사용자가 가게 주인인지 확인
    private void validateStoreOwner(Long storeId) {
        Long currentUserId = getCurrentUserId();
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다: " + storeId));
        
        if (!store.getUserId().equals(currentUserId)) {
            log.error("상품 접근 권한 없음. 가게 ID: {}, 현재 사용자 ID: {}, 가게 주인 ID: {}", 
                    storeId, currentUserId, store.getUserId());
            throw new ProductForbiddenException("해당 가게의 상품을 관리할 권한이 없습니다");
        }
    }
    
    // 상품 가격 유효성 검사
    private void validateProductPrice(Long originalPrice, Long discountedPrice) {
        if (originalPrice == null || originalPrice < 0) {
            throw new ProductInvalidPriceException("상품 원가는 0 이상이어야 합니다");
        }
        
        if (discountedPrice != null && (discountedPrice < 0 || discountedPrice > originalPrice)) {
            throw new ProductInvalidPriceException("할인 가격은 0 이상이며 원가보다 클 수 없습니다");
        }
    }
}
