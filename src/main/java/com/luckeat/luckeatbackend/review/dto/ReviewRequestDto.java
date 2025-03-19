package com.luckeat.luckeatbackend.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

	@NotNull(message = "스토어 ID는 필수입니다")
	private Long storeId;
	
	@NotNull(message = "평점은 필수입니다")
	@Min(value = 1, message = "평점은 1~5 사이여야 합니다")
	@Max(value = 5, message = "평점은 1~5 사이여야 합니다")
	private Integer rating;
	
	@NotBlank(message = "리뷰 내용은 필수입니다")
	@Size(min = 5, message = "리뷰 내용은 5글자 이상이어야 합니다")
	private String reviewContent;
	
	private String reviewImage;
}