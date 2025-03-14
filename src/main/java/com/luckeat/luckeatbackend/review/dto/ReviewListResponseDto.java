package com.luckeat.luckeatbackend.review.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponseDto {

	private String message;
	private List<ReviewResponseDto> reviews;
	private Integer totalPages;
}