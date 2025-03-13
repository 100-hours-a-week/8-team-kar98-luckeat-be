package com.luckeat.luckeatbackend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDto {

	private Integer rating;
	private String reviewContent;
	private String reviewImage;
}