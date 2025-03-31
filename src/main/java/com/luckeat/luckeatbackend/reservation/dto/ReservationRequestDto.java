package com.luckeat.luckeatbackend.reservation.dto;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.reservation.model.Reservation;
import com.luckeat.luckeatbackend.reservation.model.Reservation.ReservationStatus;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.users.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "예약 생성 요청 DTO")
public class ReservationRequestDto {
    
    @Schema(description = "상품 ID", example = "1")
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;
    
    @Schema(description = "예약 수량", example = "2")
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    private Long quantity;
    
    @Schema(description = "제로웨이스트 여부", example = "true")
    @NotNull(message = "제로웨이스트 여부는 필수입니다.")
    private Boolean isZerowaste;
    
    public Reservation toEntity(User user, Store store, Product product) {
        Long totalPrice = calculateTotalPrice(product, quantity);
        
        return Reservation.builder()
                .user(user)
                .store(store)
                .product(product)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .status(ReservationStatus.PENDING)
                .isZerowaste(isZerowaste)
                .build();
    }
    
    private Long calculateTotalPrice(Product product, Long quantity) {
        return product.getDiscountedPrice() * quantity;
    }
} 