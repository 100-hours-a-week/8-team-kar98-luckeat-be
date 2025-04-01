package com.luckeat.luckeatbackend.reservation.dto;

import java.time.LocalDateTime;

import com.luckeat.luckeatbackend.reservation.model.Reservation;
import com.luckeat.luckeatbackend.reservation.model.Reservation.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "예약 응답 DTO")
public class ReservationResponseDto {

    @Schema(description = "예약 ID")
    private Long id;
    
    @Schema(description = "사용자 ID")
    private Long userId;
    
    @Schema(description = "사용자 닉네임")
    private String userNickname;
    
    @Schema(description = "가게 ID")
    private Long storeId;
    
    @Schema(description = "상품 ID")
    private Long productId;
    
    @Schema(description = "가게 이름")
    private String storeName;
    
    @Schema(description = "상품 이름")
    private String productName;
    
    @Schema(description = "수량")
    private Long quantity;
    
    @Schema(description = "총 금액")
    private Long totalPrice;
    
    @Schema(description = "예약 상태")
    private ReservationStatus status;
    
    @Schema(description = "제로웨이스트 여부")
    private Boolean isZerowaste;
    
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
    
    public static ReservationResponseDto fromEntity(Reservation reservation) {
        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .userNickname(reservation.getUser().getNickname())
                .storeId(reservation.getStore().getId())
                .productId(reservation.getProduct().getId())
                .storeName(reservation.getStore().getStoreName())
                .productName(reservation.getProduct().getProductName())
                .quantity(reservation.getQuantity())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .isZerowaste(reservation.getIsZerowaste())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
} 