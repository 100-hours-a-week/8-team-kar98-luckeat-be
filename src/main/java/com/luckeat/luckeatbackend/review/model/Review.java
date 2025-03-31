package com.luckeat.luckeatbackend.review.model;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;
import com.luckeat.luckeatbackend.store.model.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
public class Review extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "store_id", nullable = false)
	private Long storeId;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "review_content", nullable = false, columnDefinition = "TEXT")
	private String reviewContent;

	@Column(name = "review_image", columnDefinition = "TEXT")
	private String reviewImage;

	@ManyToOne(fetch = FetchType.LAZY) // 다대일 관계
    @JoinColumn(name = "store_id", insertable = false, updatable = false) // 외래 키로 사용
    private Store store; // 가게와의 관계

}
