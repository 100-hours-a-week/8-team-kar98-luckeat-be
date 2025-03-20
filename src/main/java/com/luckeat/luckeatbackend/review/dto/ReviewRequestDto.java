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
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

	@NotNull(message = "스토어 ID는 필수입니다")
	@Schema(description = "스토어 ID", example = "123")
	private Long storeId;
	
	@NotNull(message = "평점은 필수입니다")
	@Min(value = 1, message = "평점은 1~5 사이여야 합니다")
	@Max(value = 5, message = "평점은 1~5 사이여야 합니다")
	@Schema(description = "평점 (1~5)", example = "4")
	private Integer rating;
	
	@NotBlank(message = "리뷰 내용은 필수입니다")
	@Size(min = 5, message = "리뷰 내용은 5글자 이상이어야 합니다")
	@Schema(description = "리뷰 내용", example = "이 가게는 정말 좋았습니다!")
	private String reviewContent;
	
	@Schema(description = "리뷰 이미지 URL", example = "http://example.com/review.jpg")
	private String reviewImage;
}