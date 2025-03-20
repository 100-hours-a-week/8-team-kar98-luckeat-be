package com.luckeat.luckeatbackend.review.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponseDto {

	@Schema(description = "리뷰 목록", example = "[{...}, {...}]")
	private List<ReviewResponseDto> reviews;

	@Schema(description = "총 페이지 수", example = "5")
	private Integer totalPages;
}