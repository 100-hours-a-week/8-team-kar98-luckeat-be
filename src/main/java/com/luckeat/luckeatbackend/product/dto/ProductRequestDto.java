package com.luckeat.luckeatbackend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;

import com.luckeat.luckeatbackend.product.model.Product;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidDiscountPrice(message = "할인가는 원가보다 작거나 같아야 합니다")
public class ProductRequestDto {
    private Long productId;
    
    @NotBlank(message = "상품명은 필수입니다")
    @Size(min = 1, max = 50, message = "상품명은 1자 이상 50자 이하로 입력해주세요")
    private String productName;
    
    @Pattern(regexp = "^$|^.*\\.(jpg|jpeg|png|gif)$|^(https?|ftp)://.*$", message = "이미지 파일 형식이 올바르지 않습니다")
    private String productImg;
    
    @NotNull(message = "원가는 필수입니다")
    @Positive(message = "원가는 0보다 커야 합니다")
    @Max(value = 10000000, message = "원가는 1000만원을 초과할 수 없습니다")
    private Long originalPrice;
    
    @Positive(message = "할인가는 0보다 커야 합니다")
    @Max(value = 10000000, message = "할인가는 1000만원을 초과할 수 없습니다")
    private Long discountedPrice;
    
    public Product toEntity() {
        Long finalDiscountedPrice = discountedPrice != null && discountedPrice < originalPrice
                ? discountedPrice 
                : originalPrice;
                
        return Product.builder()
                .productName(productName)
                .productImg(productImg)
                .originalPrice(originalPrice)
                .discountedPrice(finalDiscountedPrice)
                .isOpen(true) // 기본값으로 true 설정
                .build();
    }
    
    public void updateEntity(Product product) {
        product.setProductName(productName);
        product.setProductImg(productImg);
        product.setOriginalPrice(originalPrice);
        
        if (discountedPrice != null && discountedPrice < originalPrice) {
            product.setDiscountedPrice(discountedPrice);
        } else {
            product.setDiscountedPrice(originalPrice);
        }
    }
} 