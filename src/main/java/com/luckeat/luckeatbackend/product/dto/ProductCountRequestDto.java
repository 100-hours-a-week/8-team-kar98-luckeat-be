package com.luckeat.luckeatbackend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCountRequestDto {
    @NotNull(message = "상품 수량은 필수입니다")
    @Min(value = 0, message = "상품 수량은 0 이상이어야 합니다")
    @Schema(description = "상품 수량", example = "10")
    private Long count;
} 