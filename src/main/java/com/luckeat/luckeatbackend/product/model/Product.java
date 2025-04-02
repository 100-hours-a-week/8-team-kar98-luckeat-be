package com.luckeat.luckeatbackend.product.model;

import org.hibernate.annotations.SQLDelete;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;
import com.luckeat.luckeatbackend.store.model.Store;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE product SET deleted_at = NOW() WHERE id = ?")
public class Product extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Store store;

	@Column(name = "product_name", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '상품 이름'")
	private String productName;

	@Column(name = "original_price", nullable = false, columnDefinition = "BIGINT UNSIGNED COMMENT '상품 정가'")
	private Long originalPrice;

	@Column(name = "discounted_price", nullable = false, columnDefinition = "BIGINT UNSIGNED COMMENT '상품 할인 후 가격'")
	private Long discountedPrice;

	 @Column(name = "product_count", nullable = false, columnDefinition = "BIGINT UNSIGNED COMMENT '상품 갯수'")
    private Long productCount;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT COMMENT '상품설명'")
    private String description;

    @Column(name = "is_open", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '상품 판매 여부'")
    private Boolean isOpen = true;


	 public void decreaseStock(int quantity) {
        validateDecreaseStockQuantity(quantity);
        this.productCount -= quantity;
        
        // 재고가 0이 되면 isOpen을 false로 변경
        if (this.productCount == 0) {
            this.isOpen = false;
        }
    }

    public void increaseStock(int quantity) {
        validateIncreaseStockQuantity(quantity);
        this.productCount += quantity;
    }
    
    private void validateDecreaseStockQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0보다 작을 수 없습니다.");
        }
        
        if (this.productCount < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.productCount);
        }
    }
    
    private void validateIncreaseStockQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0보다 작을 수 없습니다.");
        }
        
        // 필요한 경우 최대 재고 제한을 추가할 수 있습니다
        if (this.productCount + quantity > Integer.MAX_VALUE) {
            throw new IllegalStateException("재고 최대치를 초과할 수 없습니다.");
        }
    }
}
