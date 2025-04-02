package com.luckeat.luckeatbackend.reservation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.reservation.dto.ReservationRequestDto;
import com.luckeat.luckeatbackend.reservation.dto.ReservationResponseDto;
import com.luckeat.luckeatbackend.reservation.dto.ReservationStatusRequestDto;
import com.luckeat.luckeatbackend.reservation.service.ReservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 예약 관련 API를 처리하는 컨트롤러
 * 예약 생성, 조회, 상태 변경, 취소 등의 기능 제공
 */
@RestController
@RequestMapping("api/v1/reservation")
@RequiredArgsConstructor
@Tag(name = "예약 API", description = "예약 관련 API")
public class ReservationController {

    private final ReservationService reservationService;
    
    /**
     * 새로운 예약을 생성합니다.
     * 
     * @param storeId 가게 ID
     * @param requestDto 예약 생성 정보 (상품ID, 수량, 제로웨이스트 여부)
     * @return 생성된 예약 정보와 201 Created 상태 코드
     */
    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다.")
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<ReservationResponseDto> createReservation(
            @Parameter(name = "storeId", description = "가게 ID", required = true, in = ParameterIn.PATH) 
            @PathVariable Long storeId,
            @Valid @RequestBody ReservationRequestDto requestDto) {
        ReservationResponseDto responseDto = reservationService.createReservation(storeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    /**
     * 예약 상태를 변경합니다 (확정 또는 취소).
     * 가게 소유자는 PENDING → CONFIRMED 상태로 변경할 수 있습니다.
     * 고객과 가게 소유자 모두 예약을 CANCELED 상태로 변경(취소)할 수 있습니다.
     * 
     * @param requestDto 예약 상태 변경 정보 (예약ID, 변경할 상태)
     * @return 변경된 예약 정보
     */
    @Operation(summary = "예약 상태 변경/취소", description = "예약 상태를 변경합니다. 상태 값으로 CONFIRMED 또는 CANCELED를 사용할 수 있습니다.")
    @PostMapping("/status")
    public ResponseEntity<ReservationResponseDto> updateReservationStatus(
            @Valid @RequestBody ReservationStatusRequestDto requestDto) {
        ReservationResponseDto updatedReservation = reservationService.updateReservationStatus(
                requestDto.getReservationId(), requestDto.getStatus());
        return ResponseEntity.ok(updatedReservation);
    }
    
    /**
     * 특정 가게의 모든 예약을 조회합니다 (가게 소유자용).
     * 
     * @param storeId 가게 ID
     * @return 해당 가게의 모든 예약 목록
     */
    @Operation(summary = "가게 전체 예약 목록 조회", description = "특정 가게의 모든 예약을 조회합니다. (가게 소유자용)")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<List<ReservationResponseDto>> getStoreReservations(
            @Parameter(name = "storeId", description = "가게 ID", required = true, in = ParameterIn.PATH) 
            @PathVariable Long storeId) {
        List<ReservationResponseDto> reservations = reservationService.getStoreReservations(storeId);
        return ResponseEntity.ok(reservations);
    }
    
    /**
     * 특정 가게의 대기중(PENDING) 예약을 조회합니다 (가게 소유자용).
     * 
     * @param storeId 가게 ID
     * @return 해당 가게의 대기중인 예약 목록
     */
    @Operation(summary = "가게 대기중 예약 목록 조회", description = "특정 가게의 대기중(PENDING) 예약을 조회합니다. (가게 소유자용)")
    @GetMapping("/stores/pending/{storeId}")
    public ResponseEntity<List<ReservationResponseDto>> getStorePendingReservations(
            @Parameter(name = "storeId", description = "가게 ID", required = true, in = ParameterIn.PATH) 
            @PathVariable Long storeId) {
        List<ReservationResponseDto> pendingReservations = reservationService.getStorePendingReservations(storeId);
        return ResponseEntity.ok(pendingReservations);
    }
    
    /**
     * 특정 고객의 모든 예약을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 예약 목록
     */
    @Operation(summary = "고객 예약 목록 조회", description = "특정 고객의 모든 예약을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<ReservationResponseDto>> getUserReservations(
            @Parameter(name = "userId", description = "사용자 ID", required = true, in = ParameterIn.PATH) 
            @PathVariable Long userId) {
        List<ReservationResponseDto> userReservations = reservationService.getUserReservationsById(userId);
        return ResponseEntity.ok(userReservations);
    }
} 