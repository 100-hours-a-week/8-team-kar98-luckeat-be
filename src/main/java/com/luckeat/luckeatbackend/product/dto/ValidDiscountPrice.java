package com.luckeat.luckeatbackend.product.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidDiscountPriceValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDiscountPrice {
    String message() default "할인가는 원가보다 작거나 같아야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 