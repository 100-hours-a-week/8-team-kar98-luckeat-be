package com.luckeat.luckeatbackend.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatusRequestDto {
    @NotNull(message = "상품 판매 상태는 필수입니다")
    @Schema(description = "상품 판매 상태 (open/close)", example = "true")
    private Boolean isOpen;
} 