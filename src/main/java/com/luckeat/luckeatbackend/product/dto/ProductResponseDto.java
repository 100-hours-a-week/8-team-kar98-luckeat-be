package com.luckeat.luckeatbackend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.luckeat.luckeatbackend.product.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품 이름", example = "빵")
    private String productName;

    @Schema(description = "상품 이미지 URL", example = "http://example.com/product.jpg")
    private String productImg;

    @Schema(description = "상품 원래 가격", example = "15000")
    private Long originalPrice;

    @Schema(description = "할인된 가격", example = "12000")
    private Long discountedPrice;
    
    public static ProductResponseDto fromEntity(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .productImg(product.getProductImg())
                .originalPrice(product.getOriginalPrice())
                .discountedPrice(product.getDiscountedPrice())
                .build();
    }
} 