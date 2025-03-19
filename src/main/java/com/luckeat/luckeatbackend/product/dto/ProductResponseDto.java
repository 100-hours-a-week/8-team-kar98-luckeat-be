package com.luckeat.luckeatbackend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.luckeat.luckeatbackend.product.model.Product;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long productId;
    private String productName;
    private String productImg;
    private Long originalPrice;
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