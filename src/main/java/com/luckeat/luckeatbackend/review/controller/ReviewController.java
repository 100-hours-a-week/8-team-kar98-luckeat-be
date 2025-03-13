package com.luckeat.luckeatbackend.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.luckeat.luckeatbackend.permission.service.ReviewPermissionService;
import com.luckeat.luckeatbackend.product.service.ProductService;
import com.luckeat.luckeatbackend.review.dto.MessageResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewListResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewRequestDto;
import com.luckeat.luckeatbackend.review.dto.ReviewResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewUpdateDto;
import com.luckeat.luckeatbackend.review.service.ReviewService;
import com.luckeat.luckeatbackend.users.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;
	private final ProductService productService;
	private final UserService userService;
	private final ReviewPermissionService permissionService;

	@GetMapping
	public ResponseEntity<ReviewListResponseDto> getAllReviews() {
		List<ReviewResponseDto> reviews = reviewService.getAllReviews();

		ReviewListResponseDto response = ReviewListResponseDto.builder().message("리뷰 목록 조회 성공").reviews(reviews)
				.totalPages(1).build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{review_id}")
	public ResponseEntity<?> getReviewById(@PathVariable("review_id") Long id) {
		if (reviewService.getReviewDtoById(id).isPresent()) {
			ReviewResponseDto review = reviewService.getReviewDtoById(id).get();

			ReviewListResponseDto response = ReviewListResponseDto.builder().message("리뷰 조회 성공")
					.reviews(List.of(review)).totalPages(1).build();

			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageResponseDto.error("리뷰를 찾을 수 없습니다"));
		}
	}

	@GetMapping("/user/{user_id}")
	public ResponseEntity<?> getReviewsByUser(@PathVariable("user_id") Long userId) {
		if (userService.getUserById(userId).isPresent()) {
			List<ReviewResponseDto> reviews = reviewService.getReviewsByUser(userService.getUserById(userId).get());

			ReviewListResponseDto response = ReviewListResponseDto.builder().message("사용자 리뷰 목록 조회 성공").reviews(reviews)
					.totalPages(1) // TODO: 페이지네이션 구현 시 수정 필요
					.build();

			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageResponseDto.error("사용자를 찾을 수 없습니다"));
		}
	}

	@GetMapping("/product/{product_id}")
	public ResponseEntity<?> getReviewsByProduct(@PathVariable("product_id") Long productId) {
		if (productService.getProductById(productId).isPresent()) {
			List<ReviewResponseDto> reviews = reviewService
					.getReviewsByProduct(productService.getProductById(productId).get());

			ReviewListResponseDto response = ReviewListResponseDto.builder().message("상품 리뷰 목록 조회 성공").reviews(reviews)
					.totalPages(1) // TODO: 페이지네이션 구현 시 수정 필요
					.build();

			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageResponseDto.error("상품을 찾을 수 없습니다"));
		}
	}

	@PostMapping
	public ResponseEntity<MessageResponseDto> createReview(@RequestBody ReviewRequestDto requestDto,
			@RequestParam(required = false) Long userId) {

		try {
			// TODO: 임시로 사용자 ID를 파라미터로 받음 (실제로는 인증 정보에서 가져와야 함)
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MessageResponseDto.error("인증이 필요합니다"));
			}

			if (!permissionService.hasPermission(userId, requestDto.getStoreId())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(MessageResponseDto.error("리뷰 작성 권한이 없습니다"));
			}

			reviewService.createReview(requestDto, userId);

			return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponseDto.success("리뷰 작성 성공"));
		} catch (IllegalArgumentException e) {
			log.error("리뷰 생성 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageResponseDto.error(e.getMessage()));
		} catch (Exception e) {
			log.error("리뷰 생성 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageResponseDto.error("서버 내부 오류"));
		}
	}

	@PatchMapping("/{review_id}")
	public ResponseEntity<MessageResponseDto> updateReview(@PathVariable("review_id") Long reviewId,
			@RequestBody ReviewUpdateDto updateDto, @RequestParam(required = false) Long userId) {

		try {
			// TODO: 임시로 사용자 ID를 파라미터로 받음 (실제로는 인증 정보에서 가져와야 함)
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MessageResponseDto.error("인증이 필요합니다"));
			}

			reviewService.updateReview(reviewId, updateDto, userId);

			return ResponseEntity.ok(MessageResponseDto.success("리뷰 수정 성공"));
		} catch (IllegalArgumentException e) {
			log.error("리뷰 수정 실패 (입력값 오류): {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageResponseDto.error(e.getMessage()));
		} catch (IllegalStateException e) {
			log.error("리뷰 수정 실패 (권한 또는 존재 여부): {}", e.getMessage());

			if (e.getMessage().contains("찾을 수 없습니다")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageResponseDto.error(e.getMessage()));
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(MessageResponseDto.error(e.getMessage()));
			}
		} catch (Exception e) {
			log.error("리뷰 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageResponseDto.error("서버 내부 오류"));
		}
	}

	@DeleteMapping("/{review_id}")
	public ResponseEntity<MessageResponseDto> deleteReview(@PathVariable("review_id") Long reviewId,
			@RequestParam(required = false) Long userId) {

		try {
			// TODO: 임시로 사용자 ID를 파라미터로 받음 (실제로는 인증 정보에서 가져와야 함)
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MessageResponseDto.error("인증이 필요합니다"));
			}

			reviewService.deleteReview(reviewId, userId);

			return ResponseEntity.ok(MessageResponseDto.success("리뷰 삭제 성공"));
		} catch (IllegalStateException e) {
			log.error("리뷰 삭제 실패: {}", e.getMessage());

			if (e.getMessage().contains("찾을 수 없습니다")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageResponseDto.error(e.getMessage()));
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(MessageResponseDto.error(e.getMessage()));
			}
		} catch (Exception e) {
			log.error("리뷰 삭제 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageResponseDto.error("서버 내부 오류"));
		}
	}
}
