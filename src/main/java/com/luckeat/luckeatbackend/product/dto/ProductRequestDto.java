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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import com.luckeat.luckeatbackend.product.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidDiscountPrice(message = "할인가는 원가보다 작거나 같아야 합니다")
public class ProductRequestDto {
    @NotBlank(message = "상품명은 필수입니다")
    @Size(min = 1, max = 50, message = "상품명은 1자 이상 50자 이하로 입력해주세요")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]*$", message = "상품명은 한글, 영문, 숫자만 입력 가능합니다")
    @Schema(description = "상품 이름", example = "맛있는 빵")
    private String productName;
    
    @NotNull(message = "원가는 필수입니다")
    @Positive(message = "원가는 0보다 커야 합니다")
    @Min(value = 100, message = "원가는 최소 100원 이상이어야 합니다")
    @Max(value = 10000000, message = "원가는 1000만원을 초과할 수 없습니다")
    @Schema(description = "상품 가격", example = "10000")
    private Long originalPrice;
    
    @Positive(message = "할인가는 0보다 커야 합니다")
    @Min(value = 100, message = "할인가는 최소 100원 이상이어야 합니다")
    @Max(value = 10000000, message = "할인가는 1000만원을 초과할 수 없습니다")
    @Schema(description = "할인가", example = "8000")
    private Long discountedPrice;

    @NotNull(message = "상품 수량은 필수입니다")
    @Positive(message = "상품 수량은 0보다 커야 합니다")
    @Max(value = 999, message = "상품 수량은 999개를 초과할 수 없습니다")
    @Schema(description = "상품 수량", example = "10")
    private Long productCount;
    
    @NotBlank(message = "상품 설명은 필수입니다")
    @Size(min = 10, max = 1000, message = "상품 설명은 10자 이상 1000자 이하로 입력해주세요")
    @Schema(description = "상품 설명", example = "신선한 재료로 매일 아침 직접 굽는 맛있는 빵입니다! 100% 우유버터를 사용하여 더욱 고소하고 풍미가 좋습니다. :)")
    private String description;

    @Schema(description = "상품 판매 여부", example = "true")
    private Boolean isOpen;
    
    public Product toEntity() {
        Long finalDiscountedPrice = discountedPrice != null && discountedPrice < originalPrice
                ? discountedPrice 
                : originalPrice;
                
        return Product.builder()
                .productName(productName)
                .originalPrice(originalPrice)
                .discountedPrice(finalDiscountedPrice)
                .productCount(productCount)
                .description(description)
                .isOpen(isOpen != null ? isOpen : true)
                .build();
    }
    
    public void updateEntity(Product product) {
        product.setProductName(productName);
        product.setOriginalPrice(originalPrice);
        product.setProductCount(productCount);
        product.setDescription(description);
        
        if (discountedPrice != null && discountedPrice < originalPrice) {
            product.setDiscountedPrice(discountedPrice);
        } else {
            product.setDiscountedPrice(originalPrice);
        }
    }
} 