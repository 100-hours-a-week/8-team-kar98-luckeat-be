package com.luckeat.luckeatbackend.review.dto;

import java.time.LocalDateTime;

import com.luckeat.luckeatbackend.review.model.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

	@Schema(description = "리뷰 ID", example = "1")
	private Long reviewId;

	@Schema(description = "사용자 ID", example = "42")
	private Long userId;

	@Schema(description = "가게명", example = "맛있는 식당")
	private String storeName;

	@Schema(description = "상품명", example = "특제 비빔밥")
	private String productName;

	@Schema(description = "총 가격", example = "15000")
	private Long totalPrice;

	@Schema(description = "상품 주문 개수", example = "2")
	private Long quantity;

	@Schema(description = "평점", example = "4")
	private Integer rating;

	@Schema(description = "리뷰 내용", example = "이 가게는 정말 좋았습니다!")
	private String reviewContent;

	@Schema(description = "리뷰 이미지 URL", example = "http://example.com/review.jpg")
	private String reviewImage;

	@Schema(description = "리뷰 작성 시간", example = "2023-03-20T12:34:56")
	private LocalDateTime createdAt;

	public static ReviewResponseDto fromEntity(Review review) {
		return ReviewResponseDto.builder()
				.reviewId(review.getId())
				.userId(review.getUserId())
				.storeName(review.getStore().getStoreName())
				.productName(review.getReservation().getProduct().getProductName())
				.totalPrice(review.getReservation().getTotalPrice())
				.quantity(review.getReservation().getQuantity())
				.rating(review.getRating())
				.reviewContent(review.getReviewContent())
				.reviewImage(review.getReviewImage())
				.createdAt(review.getCreatedAt())
				.build();
	}
}