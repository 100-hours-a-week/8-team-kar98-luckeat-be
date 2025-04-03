package com.luckeat.luckeatbackend.reservation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.common.exception.product.ProductNotFoundException;
import com.luckeat.luckeatbackend.common.exception.reservation.ReservationNotFoundException;
import com.luckeat.luckeatbackend.common.exception.store.StoreNotFoundException;
import com.luckeat.luckeatbackend.common.exception.user.UserNotFoundException;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.product.repository.ProductRepository;
import com.luckeat.luckeatbackend.reservation.dto.ReservationRequestDto;
import com.luckeat.luckeatbackend.reservation.dto.ReservationResponseDto;
import com.luckeat.luckeatbackend.reservation.model.Reservation;
import com.luckeat.luckeatbackend.reservation.model.Reservation.ReservationStatus;
import com.luckeat.luckeatbackend.reservation.repository.ReservationRepository;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.store.repository.StoreRepository;
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    
    /**
     * 새로운 예약 생성
     */
    @Transactional
    public ReservationResponseDto createReservation(Long storeId, ReservationRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));
        
         
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다."));

        // 상품이 해당 가게의 것인지 확인
        if (!product.getStore().getId().equals(storeId)) {
            throw new ProductNotFoundException("해당 가게의 상품이 아닙니다.");
        }
        
        if (product.getProductCount() < requestDto.getQuantity()) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        product.decreaseStock(requestDto.getQuantity().intValue());
        
        
        Reservation reservation = requestDto.toEntity(user, store, product);
        Reservation savedReservation = reservationRepository.save(reservation);
        
       
        return ReservationResponseDto.fromEntity(savedReservation);
    }
    
    /**
     * 사용자의 모든 예약 조회 (ID로 조회)
     */
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getUserReservationsById(Long userId) {
        // 사용자 권한 확인 로직 추가 필요 
        // (현재 로그인한 사용자가 요청한 userId와 동일한지 또는 관리자인지)
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        // deleted_at이 null이 아닌 예약(취소된 예약)도 포함하여 모든 예약 조회
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        
        return reservations.stream()
                .map(ReservationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 가게의 모든 예약 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getStoreReservations(Long storeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));
        
        // 가게 소유자 확인 로직 추가 필요
        
        // deleted_at이 null이 아닌 예약(취소된 예약)도 포함하여 모든 예약 조회
        List<Reservation> reservations = reservationRepository.findByStoreId(storeId);
        
        return reservations.stream()
                .map(ReservationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 가게의 대기중(PENDING) 예약 조회
     */
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getStorePendingReservations(Long storeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException("가게를 찾을 수 없습니다."));
        
        // 가게 소유자 확인 로직 추가 필요
        
        // deleted_at이 null이 아닌 예약(취소된 예약)도 포함하여 PENDING 상태 예약 조회
        List<Reservation> reservations = reservationRepository.findByStoreIdAndStatus(
                storeId, ReservationStatus.PENDING);
        
        return reservations.stream()
                .map(ReservationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 예약 상태 변경
     * 가게 소유자는 PENDING → CONFIRMED로 변경 가능
     * 사용자와 가게 소유자 모두 CANCELED 상태로 변경 가능
     */
    @Transactional
    public ReservationResponseDto updateReservationStatus(Long reservationId, ReservationStatus status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));
        
        // 사용자 권한 체크
        boolean isStoreOwner = reservation.getStore().getUserId().equals(user.getId());
        boolean isReservationUser = reservation.getUser().getId().equals(user.getId());
        
        // 이미 삭제된 예약인지 확인
        if (reservation.getDeletedAt() != null) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        // 권한 검증
        if (status == ReservationStatus.CONFIRMED) {
            // CONFIRMED는 가게 소유자만 가능
            if (!isStoreOwner) {
                throw new SecurityException("예약 상태를 변경할 권한이 없습니다.");
            }
        reservation.setStatus(status);
        reservation.setDeletedAt(LocalDateTime.now());
        
        // 유저의 총 상품 갯수와 절약 금액 업데이트
        User reservationUser = reservation.getUser();
        Product product = reservation.getProduct();
        
        int quantity = reservation.getQuantity().intValue();
        // 총 상품 갯수 증가 (예약한 수량만큼 증가)
        reservationUser.setTotalProductCount(
            reservationUser.getTotalProductCount() + quantity
        );
        
        // 절약 금액 계산 및 추가
        long savedMoney = ((long) product.getOriginalPrice() - product.getDiscountedPrice()) * quantity;
        reservationUser.setTotalSavedMoney(reservationUser.getTotalSavedMoney() + (int) savedMoney);
        userRepository.save(reservationUser);
        } else if (status == ReservationStatus.CANCELED) {
            // CANCELED는 예약자 본인 또는 가게 소유자만 가능
            if (!isReservationUser && !isStoreOwner) {
                throw new SecurityException("예약을 취소할 권한이 없습니다.");
            }

            // 재고 증가 처리
            Product product = reservation.getProduct();
            int quantity = reservation.getQuantity().intValue();
            
            // 현재 재고가 0이고, 취소된 수량이 0보다 크다면 isOpen을 true로 변경
            if (product.getProductCount() == 0 && quantity > 0) {
                product.setIsOpen(true);
            }
            
            product.increaseStock(quantity);
            
            // 예약 취소 처리
            reservation.setStatus(status);
            reservation.setDeletedAt(LocalDateTime.now());  // 취소 시간 설정
        } else {
            throw new IllegalArgumentException("유효하지 않은 예약 상태입니다.");
        }
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        return ReservationResponseDto.fromEntity(updatedReservation);
    }
    
    /**
     * 예약 삭제 (소프트 딜리트)
     */
    @Transactional
    public void deleteReservation(Long reservationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        Reservation reservation = reservationRepository.findByIdAndUserIdAndDeletedAtIsNull(reservationId, user.getId())
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));
        
        reservation.setDeletedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }

    @Transactional
    public void updateReviewStatus(Long reservationId, boolean isReviewed) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));
        
        reservation.setIsReviewed(isReviewed);
        reservationRepository.save(reservation);
    }
} 