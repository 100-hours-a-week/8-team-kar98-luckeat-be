package com.luckeat.luckeatbackend.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import com.luckeat.luckeatbackend.permission.service.ReviewPermissionService;
import com.luckeat.luckeatbackend.review.dto.MessageResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewListResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewRequestDto;
import com.luckeat.luckeatbackend.review.dto.ReviewResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewUpdateDto;
import com.luckeat.luckeatbackend.review.service.ReviewService;
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;
	private final UserService userService;
	private final ReviewPermissionService permissionService;

	@GetMapping
	public ResponseEntity<ReviewListResponseDto> getAllReviews() {
		List<ReviewResponseDto> reviews = reviewService.getAllReviews();

		// 리뷰가 없는 경우 예외 발생
		if (reviews.isEmpty()) {
			throw new ReviewNotFoundException();
		}

		ReviewListResponseDto response = ReviewListResponseDto.builder().message("리뷰 목록 조회 성공").reviews(reviews)
				.totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{review_id}")
	public ResponseEntity<ReviewListResponseDto> getReviewById(@PathVariable("review_id") Long id) {
		// ID로 리뷰를 찾고, 없으면 ReviewNotFoundException 발생
		ReviewResponseDto review = reviewService.getReviewDtoById(id).orElseThrow(() -> new ReviewNotFoundException()); // ErrorCode.REVIEW_NOT_FOUND
																														// 사용

		// 리뷰 정보를 응답으로 구성
		ReviewListResponseDto response = ReviewListResponseDto.builder().message("리뷰 조회 성공").reviews(List.of(review))
				.totalPages(1).build();

		return ResponseEntity.ok(response);
		// 예외는 GlobalExceptionHandler로 전달됩니다
	}

	@GetMapping("/store/{store_id}")
	public ResponseEntity<ReviewListResponseDto> getReviewsByStore(@PathVariable("store_id") Long storeId) {
		List<ReviewResponseDto> reviews = reviewService.getReviewsByStoreId(storeId);

		if (reviews.isEmpty()) {
			throw new ReviewNotFoundException("해당 가게의 리뷰가 없습니다");
		}

		ReviewListResponseDto response = ReviewListResponseDto.builder().message("가게 리뷰 목록 조회 성공").reviews(reviews)
				.totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/{user_id}")
	public ResponseEntity<ReviewListResponseDto> getReviewsByUser(@PathVariable("user_id") Long userId) {
		List<ReviewResponseDto> reviews = reviewService.getReviewsByUserId(userId);

		if (reviews.isEmpty()) {
			throw new ReviewNotFoundException("해당 사용자의 리뷰가 없습니다");
		}
		ReviewListResponseDto response = ReviewListResponseDto.builder().message("사용자 리뷰 목록 조회 성공").reviews(reviews)
				.totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<MessageResponseDto> createReview(@RequestBody ReviewRequestDto requestDto,
			@AuthenticationPrincipal UserDetails userDetails) {

		// UserDetails에서 이메일 가져오기
		String email = userDetails.getUsername();

		// UserService를 통해 이메일로 사용자 조회
		User user = userService.getUserByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

		Long userId = user.getId();

		try {
			// 권한 검사는 서비스 레이어에서 처리하도록 이동할 수 있음
			if (!permissionService.hasPermission(userId, requestDto.getStoreId())) {
				throw new ReviewForbiddenException("리뷰 작성 권한이 없습니다");
			}

			// 리뷰 생성
			reviewService.createReview(requestDto, userId);

			// 상태 코드만 반환
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (ReviewInvalidContentException | ReviewInvalidRatingException | ReviewInvalidImageException
				| ReviewForbiddenException e) {
			log.warn("리뷰 생성 중 검증 오류 발생: {}", e.getMessage());
			throw e;
		}
	}

	@PutMapping("/{review_id}")
	public ResponseEntity<MessageResponseDto> updateReview(@PathVariable("review_id") Long reviewId,
			@RequestBody ReviewUpdateDto updateDto, @AuthenticationPrincipal UserDetails userDetails) {

		// 사용자 이메일 가져오기
		String email = userDetails.getUsername();

		// 사용자 ID 조회
		Long userId = userService.getUserByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email)).getId();

		try {
			// 리뷰 수정
			reviewService.updateReview(reviewId, updateDto, userId);
			return ResponseEntity.ok(MessageResponseDto.success("리뷰 수정 성공"));
		} catch (ReviewInvalidContentException | ReviewInvalidRatingException | ReviewInvalidImageException e) {
			log.warn("리뷰 수정 중 검증 오류 발생: {}", e.getMessage());
			throw e;
		} catch (ReviewNotFoundException e) {
			log.warn("수정할 리뷰를 찾을 수 없음: {}", e.getMessage());
			throw e;
		} catch (ReviewForbiddenException e) {
			log.warn("리뷰 수정 권한 없음: {}", e.getMessage());
			throw e;
		}
	}

	@DeleteMapping("/{review_id}")
	public ResponseEntity<MessageResponseDto> deleteReview(@PathVariable("review_id") Long reviewId,
			@AuthenticationPrincipal UserDetails userDetails) {

		// 사용자 이메일 가져오기
		String email = userDetails.getUsername();

		// 사용자 ID 조회
		Long userId = userService.getUserByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email)).getId();

		try {
			// 리뷰 삭제
			reviewService.deleteReview(reviewId, userId);
			return ResponseEntity.ok(MessageResponseDto.success("리뷰 삭제 성공"));
		} catch (ReviewNotFoundException e) {
			log.warn("삭제할 리뷰를 찾을 수 없음: {}", e.getMessage());
			throw e;
		} catch (ReviewForbiddenException e) {
			log.warn("리뷰 삭제 권한 없음: {}", e.getMessage());
			throw e;
		}
	}
}
