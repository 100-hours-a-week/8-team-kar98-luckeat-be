package com.luckeat.luckeatbackend.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.luckeat.luckeatbackend.permission.service.ReviewPermissionService;
import com.luckeat.luckeatbackend.product.service.ProductService;
import com.luckeat.luckeatbackend.review.model.Review;
import com.luckeat.luckeatbackend.review.service.ReviewService;
import com.luckeat.luckeatbackend.users.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
	private final ProductService productService;
	private final UserService userService;
	private final ReviewPermissionService permissionService;

	@GetMapping
	public ResponseEntity<List<Review>> getAllReviews() {
		return ResponseEntity.ok(reviewService.getAllReviews());
	}

	@GetMapping("/{review_id}")
	public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
		return reviewService.getReviewById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// TODO: 유저ID로 리뷰 조회
	@GetMapping("/user/{user_id}")
	public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
		return userService.getUserById(userId).map(user -> ResponseEntity.ok(reviewService.getReviewsByUser(user)))
				.orElse(ResponseEntity.notFound().build());
	}

	// TODO: 상품ID로 리뷰 조회
	@GetMapping("/product/{product_id}")
	public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
		return productService.getProductById(productId)
				.map(product -> ResponseEntity.ok(reviewService.getReviewsByProduct(product)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> createReview(@RequestBody Review review) {
		return userService.getUserById(review.getUser().getId())
				.flatMap(user -> productService.getProductById(review.getProduct().getId()).map(product -> {
					if (permissionService.canUserReview(user, product)) {
						review.setUser(user);
						review.setProduct(product);
						return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(review));
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN)
								.body("User does not have permission to review this product");
					}
				})).orElse(ResponseEntity.notFound().build());
	}

	@PatchMapping("/{review_id}")
	public ResponseEntity<?> updateReview(@RequestBody Review review) {
		return userService.getUserById(review.getUser().getId())
				.flatMap(user -> productService.getProductById(review.getProduct().getId()).map(product -> {
					if (permissionService.canUserReview(user, product)) {
						review.setUser(user);
						review.setProduct(product);
						return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(review));
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN)
								.body("User does not have permission to review this product");
					}
				})).orElse(ResponseEntity.notFound().build());
	}

	// TODO: 리뷰 수정 구현
	@PatchMapping
	public ResponseEntity<?> updateReview(@RequestBody Review review) {
		return userService.getUserById(review.getUser().getId())
				.flatMap(user -> productService.getProductById(review.getProduct().getId()).map(product -> {
					if (permissionService.canUserReview(user, product)) {
						review.setUser(user);
						review.setProduct(product);
						// 기존 리뷰 업데이트: reviewService.updateReview(review) 메서드를 호출
						return ResponseEntity.ok(reviewService.updateReview(review));
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN)
								.body("User does not have permission to review this product");
					}
				})).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{review_id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		return reviewService.getReviewById(id).map(review -> {
			reviewService.deleteReview(id);
			return ResponseEntity.noContent().<Void>build();
		}).orElse(ResponseEntity.notFound().build());
	}
}
