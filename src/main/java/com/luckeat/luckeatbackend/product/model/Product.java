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

}
