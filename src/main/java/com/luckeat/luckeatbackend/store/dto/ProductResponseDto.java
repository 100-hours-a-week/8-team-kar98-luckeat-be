package com.luckeat.luckeatbackend.store.dto;

import com.luckeat.luckeatbackend.product.model.Product;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "상품 응답 DTO")
public class ProductResponseDto {
	@Schema(description = "상품 ID", example = "1")
	private Long id;
	
	@Schema(description = "상품명", example = "특별 국수")
	private String productName;
	
	@Schema(description = "상품 이미지 URL", example = "https://example.com/product.jpg")
	private String productImg;
	
	@Schema(description = "원래 가격", example = "10000")
	private Long originalPrice;
	
	@Schema(description = "할인된 가격", example = "8000")
	private Long discountedPrice;
	
	@Schema(description = "할인 중인지 여부", example = "true")
	private Boolean isOpen;

	public static ProductResponseDto fromEntity(Product product) {
		return ProductResponseDto.builder().id(product.getId()).productName(product.getProductName())
				.productImg(product.getProductImg()).originalPrice(product.getOriginalPrice())
				.discountedPrice(product.getDiscountedPrice()).isOpen(product.getIsOpen()).build();
	}
}