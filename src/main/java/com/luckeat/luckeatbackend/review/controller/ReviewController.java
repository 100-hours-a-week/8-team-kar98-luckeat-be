package com.luckeat.luckeatbackend.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.luckeat.luckeatbackend.common.exception.review.ReviewForbiddenException;
import com.luckeat.luckeatbackend.common.exception.review.ReviewInvalidContentException;
import com.luckeat.luckeatbackend.common.exception.review.ReviewInvalidImageException;
import com.luckeat.luckeatbackend.common.exception.review.ReviewInvalidRatingException;
import com.luckeat.luckeatbackend.common.exception.review.ReviewNotFoundException;
import com.luckeat.luckeatbackend.review.dto.ReviewListResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewRequestDto;
import com.luckeat.luckeatbackend.review.dto.ReviewResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewUpdateDto;
import com.luckeat.luckeatbackend.review.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "리뷰 API", description = "리뷰 관련 API 목록")
public class ReviewController {

	private final ReviewService reviewService;

	@Operation(summary = "모든 리뷰 조회", description = "모든 리뷰를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
		@ApiResponse(responseCode = "404", description = "리뷰가 하나도 없음", content = @Content)
	})
	@GetMapping
	public ResponseEntity<ReviewListResponseDto> getAllReviews() {
		List<ReviewResponseDto> reviews = reviewService.getAllReviews();

		// 리뷰가 없는 경우 예외 발생
		if (reviews.isEmpty()) {
			throw new ReviewNotFoundException();
		}

		// TODO: 페이지네이션 구현 시 실제 페이지 수 계산 로직 추가 필요
		// int totalPages = (int) Math.ceil((double) totalCount / pageSize);
		ReviewListResponseDto response = ReviewListResponseDto.builder().reviews(reviews).totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "특정 리뷰 조회", description = "특정 ID의 리뷰를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content)
	})
	@GetMapping("/{review_id}")
	public ResponseEntity<ReviewListResponseDto> getReviewById(@PathVariable("review_id") Long id) {
		// ID로 리뷰를 찾고, 없으면 ReviewNotFoundException 발생
		ReviewResponseDto review = reviewService.getReviewDtoById(id).orElseThrow(() -> new ReviewNotFoundException());

		// TODO: 페이지네이션 구현 시 수정 필요 (단일 리뷰 조회는 항상 1페이지)
		ReviewListResponseDto response = ReviewListResponseDto.builder().reviews(List.of(review)).totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "가게의 모든 리뷰 조회", description = "특정 가게의 모든 리뷰를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "가게 리뷰 목록 조회 성공"),
		@ApiResponse(responseCode = "404", description = "가게에 리뷰가 없음", content = @Content)
	})
	@GetMapping("/store/{store_id}")
	public ResponseEntity<ReviewListResponseDto> getReviewsByStore(@PathVariable("store_id") Long storeId) {
		List<ReviewResponseDto> reviews = reviewService.getReviewsByStoreId(storeId);

		if (reviews.isEmpty()) {
			throw new ReviewNotFoundException("해당 가게의 리뷰가 없습니다");
		}

		// TODO: 페이지네이션 구현 시 가게별 리뷰 총 개수와 페이지 크기에 따라 totalPages 계산 필요
		// int totalPages = (int) Math.ceil((double) totalReviewCount / pageSize);
		ReviewListResponseDto response = ReviewListResponseDto.builder().reviews(reviews).totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "내가 작성한 리뷰 조회", description = "현재 인증된 사용자가 작성한 모든 리뷰를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "내 리뷰 목록 조회 성공"),
		@ApiResponse(responseCode = "404", description = "작성한 리뷰가 없음", content = @Content)
	})
	@GetMapping("/my-reviews")
	public ResponseEntity<ReviewListResponseDto> getMyReviews() {
		List<ReviewResponseDto> reviews = reviewService.getMyReviews();

		if (reviews.isEmpty()) {
			throw new ReviewNotFoundException("작성한 리뷰가 없습니다");
		}

		// TODO: 페이지네이션 구현 시 내 리뷰 총 개수와 페이지 크기에 따라 totalPages 계산 필요
		// int totalPages = (int) Math.ceil((double) myReviewCount / pageSize);
		ReviewListResponseDto response = ReviewListResponseDto.builder().reviews(reviews).totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "리뷰 생성", description = "새로운 리뷰를 생성합니다 (인증된 사용자만 가능).")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "리뷰 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
		@ApiResponse(responseCode = "403", description = "리뷰 작성 권한 없음", content = @Content)
	})
	@PostMapping
	public ResponseEntity<Void> createReview(@Valid @RequestBody ReviewRequestDto requestDto) {
		// 리뷰 생성 - 예외는 GlobalExceptionHandler에서 처리
		reviewService.createReview(requestDto);
		// 상태 코드만 반환
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다 (작성자만 수정 가능).")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content),
		@ApiResponse(responseCode = "403", description = "리뷰 수정 권한 없음", content = @Content)
	})
	@PutMapping("/{review_id}")
	public ResponseEntity<Void> updateReview(
			@PathVariable("review_id") Long reviewId,
			@Valid @RequestBody ReviewUpdateDto updateDto) {
		// 리뷰 수정 - 예외는 GlobalExceptionHandler에서 처리
		reviewService.updateReview(reviewId, updateDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다 (작성자만 삭제 가능, 소프트 삭제 방식).")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content),
		@ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음", content = @Content)
	})
	@DeleteMapping("/{review_id}")
	public ResponseEntity<Void> deleteReview(@PathVariable("review_id") Long reviewId) {
		// 리뷰 삭제 - 예외는 GlobalExceptionHandler에서 처리
		reviewService.deleteReview(reviewId);
		return ResponseEntity.noContent().build();
	}
}
