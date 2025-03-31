package com.luckeat.luckeatbackend.reservation.dto;

import com.luckeat.luckeatbackend.reservation.model.Reservation.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "예약 상태 변경 요청 DTO")
public class ReservationStatusRequestDto {
    
    @Schema(description = "예약 ID", example = "1")
    @NotNull(message = "예약 ID는 필수입니다.")
    private Long reservationId;
    
    @Schema(description = "변경할 상태 (CONFIRMED 또는 CANCELED)", example = "CONFIRMED")
    @NotNull(message = "변경할 상태는 필수입니다.")
    private ReservationStatus status;
} 