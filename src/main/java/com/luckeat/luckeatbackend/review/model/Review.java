package com.luckeat.luckeatbackend.review.model;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

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

}
