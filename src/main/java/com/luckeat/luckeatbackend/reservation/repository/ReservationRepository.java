package com.luckeat.luckeatbackend.reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.luckeat.luckeatbackend.reservation.model.Reservation;
import com.luckeat.luckeatbackend.reservation.model.Reservation.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByUserId(Long userId);
    
    List<Reservation> findByStoreId(Long storeId);
    
    List<Reservation> findByProductId(Long productId);
    
    List<Reservation> findByStoreIdAndStatus(Long storeId, ReservationStatus status);
    
    List<Reservation> findByUserIdAndDeletedAtIsNull(Long userId);
    
    List<Reservation> findByStoreIdAndDeletedAtIsNull(Long storeId);
    
    List<Reservation> findByStoreIdAndStatusAndDeletedAtIsNull(Long storeId, ReservationStatus status);
    
    Optional<Reservation> findByIdAndUserId(Long id, Long userId);
    
    Optional<Reservation> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
    
    Optional<Reservation> findByIdAndDeletedAtIsNull(Long id);
    
    List<Reservation> findByDeletedAtIsNull();
} 