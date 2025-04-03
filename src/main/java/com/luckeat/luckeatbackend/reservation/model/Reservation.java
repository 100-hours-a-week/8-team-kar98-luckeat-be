package com.luckeat.luckeatbackend.reservation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.store.model.Store;
import com.luckeat.luckeatbackend.users.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE reservation SET deleted_at = NOW() WHERE id = ?")
public class Reservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false, columnDefinition = "BIGINT UNSIGNED COMMENT '예약 수량'")
    private Long quantity;

    @Column(name = "total_price", nullable = false, columnDefinition = "BIGINT UNSIGNED COMMENT '총 가격'")
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDING', 'CONFIRMED', 'CANCELED') COMMENT '예약 상태'")
    private ReservationStatus status;

    @Column(name = "is_zerowaste", nullable = false, columnDefinition = "BOOLEAN COMMENT '제로웨이스트 여부'")
    private Boolean isZerowaste;

    @Column(name = "is_reviewed", nullable = false, columnDefinition = "BOOLEAN COMMENT '리뷰 작성 여부'")
    private Boolean isReviewed;

    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELED //COMPLETED
    }
} 