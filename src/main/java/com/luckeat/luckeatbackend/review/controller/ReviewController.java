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

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * 모든 리뷰를 조회합니다
	 * 
	 * @return 모든 리뷰 목록과 페이지 정보가 포함된 응답
	 * @throws ReviewNotFoundException
	 *             리뷰가 하나도 없을 경우 발생
	 */
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

	/**
	 * 특정 ID의 리뷰를 조회합니다
	 * 
	 * @param id
	 *            조회할 리뷰의 ID
	 * @return 조회된 리뷰 정보가 포함된 응답
	 * @throws ReviewNotFoundException
	 *             해당 ID의 리뷰가 없거나 삭제된 경우 발생
	 */
	@GetMapping("/{review_id}")
	public ResponseEntity<ReviewListResponseDto> getReviewById(@PathVariable("review_id") Long id) {
		// ID로 리뷰를 찾고, 없으면 ReviewNotFoundException 발생
		ReviewResponseDto review = reviewService.getReviewDtoById(id).orElseThrow(() -> new ReviewNotFoundException());

		// TODO: 페이지네이션 구현 시 수정 필요 (단일 리뷰 조회는 항상 1페이지)
		ReviewListResponseDto response = ReviewListResponseDto.builder().reviews(List.of(review)).totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	/**
	 * 특정 가게의 모든 리뷰를 조회합니다
	 * 
	 * @param storeId
	 *            리뷰를 조회할 가게의 ID
	 * @return 가게에 대한 리뷰 목록과 페이지 정보가 포함된 응답
	 * @throws ReviewNotFoundException
	 *             해당 가게에 리뷰가 하나도 없을 경우 발생
	 */
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

	/**
	 * 현재 인증된 사용자가 작성한 모든 리뷰를 조회합니다
	 * 
	 * @return 사용자가 작성한 리뷰 목록과 페이지 정보가 포함된 응답
	 * @throws ReviewNotFoundException
	 *             사용자가 작성한 리뷰가 하나도 없을 경우 발생
	 */
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

	/**
	 * 새로운 리뷰를 생성합니다 (인증된 사용자만 가능)
	 * 
	 * @param requestDto 리뷰 생성에 필요한 정보 (가게ID, 평점, 리뷰내용, 이미지URL 등)
	 * @return 생성 성공 시 201 Created 상태 코드 반환 (본문 없음)
	 * @throws ReviewInvalidContentException 리뷰 내용이 유효하지 않을 경우 발생
	 * @throws ReviewInvalidRatingException 평점이 유효하지 않을 경우 발생
	 * @throws ReviewInvalidImageException 이미지 URL이 유효하지 않을 경우 발생
	 * @throws ReviewForbiddenException 사용자에게 리뷰 작성 권한이 없을 경우 발생
	 */
	@PostMapping
	public ResponseEntity<Void> createReview(@Valid @RequestBody ReviewRequestDto requestDto) {
		// 리뷰 생성 - 예외는 GlobalExceptionHandler에서 처리
		reviewService.createReview(requestDto);
		// 상태 코드만 반환
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 기존 리뷰를 수정합니다 (작성자만 수정 가능)
	 * 
	 * @param reviewId 수정할 리뷰의 ID
	 * @param updateDto 수정할 내용 (평점, 리뷰내용, 이미지URL 등)
	 * @return 수정 성공 시 201 Created 상태 코드 반환 (본문 없음)
	 * @throws ReviewNotFoundException 해당 ID의 리뷰가 없거나 삭제된 경우 발생
	 * @throws ReviewForbiddenException 작성자가 아닌 사용자가 수정을 시도할 경우 발생
	 * @throws ReviewInvalidContentException 리뷰 내용이 유효하지 않을 경우 발생
	 * @throws ReviewInvalidRatingException 평점이 유효하지 않을 경우 발생
	 * @throws ReviewInvalidImageException 이미지 URL이 유효하지 않을 경우 발생
	 */
	@PutMapping("/{review_id}")
	public ResponseEntity<Void> updateReview(
			@PathVariable("review_id") Long reviewId,
			@Valid @RequestBody ReviewUpdateDto updateDto) {
		// 리뷰 수정 - 예외는 GlobalExceptionHandler에서 처리
		reviewService.updateReview(reviewId, updateDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 리뷰를 삭제합니다 (작성자만 삭제 가능, 소프트 삭제 방식)
	 * 
	 * @param reviewId 삭제할 리뷰의 ID
	 * @return 삭제 성공 시 204 No Content 상태 코드 반환 (본문 없음)
	 * @throws ReviewNotFoundException 해당 ID의 리뷰가 없거나 이미 삭제된 경우 발생
	 * @throws ReviewForbiddenException 작성자가 아닌 사용자가 삭제를 시도할 경우 발생
	 */
	@DeleteMapping("/{review_id}")
	public ResponseEntity<Void> deleteReview(@PathVariable("review_id") Long reviewId) {
		// 리뷰 삭제 - 예외는 GlobalExceptionHandler에서 처리
		reviewService.deleteReview(reviewId);
		return ResponseEntity.noContent().build();
	}
}
