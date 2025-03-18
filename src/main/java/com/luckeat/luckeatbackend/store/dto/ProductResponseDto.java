package com.luckeat.luckeatbackend.store.dto;

import com.luckeat.luckeatbackend.product.model.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상품 응답 DTO (가게 상세 정보에 포함)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
	private Long id;
	private String productName;
	private String productImg;
	private Long originalPrice;
	private Long discountedPrice;
	private Boolean isOpen;

	public static ProductResponseDto fromEntity(Product product) {
		return ProductResponseDto.builder().id(product.getId()).productName(product.getProductName())
				.productImg(product.getProductImg()).originalPrice(product.getOriginalPrice())
				.discountedPrice(product.getDiscountedPrice()).isOpen(product.getIsOpen()).build();
	}
}