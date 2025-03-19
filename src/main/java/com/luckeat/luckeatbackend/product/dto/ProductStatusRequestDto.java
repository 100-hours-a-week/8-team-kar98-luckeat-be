package com.luckeat.luckeatbackend.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatusRequestDto {
    @NotNull(message = "상품 판매 상태는 필수입니다")
    private Boolean isOpen;
} 