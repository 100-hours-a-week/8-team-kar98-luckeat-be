package com.luckeat.luckeatbackend.product.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDiscountPriceValidator implements ConstraintValidator<ValidDiscountPrice, ProductRequestDto> {
    @Override
    public boolean isValid(ProductRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getDiscountedPrice() == null || dto.getOriginalPrice() == null) {
            return true;
        }
        
        return dto.getDiscountedPrice() <= dto.getOriginalPrice();
    }
} 