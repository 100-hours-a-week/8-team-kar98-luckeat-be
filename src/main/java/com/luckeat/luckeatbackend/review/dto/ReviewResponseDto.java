package com.luckeat.luckeatbackend.review.dto;

import java.time.LocalDateTime;

import com.luckeat.luckeatbackend.review.model.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

	private Long reviewId;
	private Long userId;
	private Integer rating;
	private String reviewContent;
	private String reviewImage;
	private LocalDateTime createdAt;

	public static ReviewResponseDto fromEntity(Review review) {
		return ReviewResponseDto.builder().reviewId(review.getId()).userId(review.getUserId())
				.rating(review.getRating()).reviewContent(review.getReviewContent())
				.reviewImage(review.getReviewImage()).createdAt(review.getCreatedAt()).build();
	}
}