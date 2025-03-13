package com.luckeat.luckeatbackend.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.review.model.Review;
import com.luckeat.luckeatbackend.review.repository.ReviewRepository;
import com.luckeat.luckeatbackend.users.model.User;

import lombok.RequiredArgsConstructor;

/**
 * 리뷰 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;

	/**
	 * 모든 리뷰 조회
	 */
	public List<Review> getAllReviews() {
		return reviewRepository.findAll();
	}

	/**
	 * ID로 특정 리뷰 조회
	 */
	public Optional<Review> getReviewById(Long id) {
		return reviewRepository.findById(id);
	}

	/**
	 * 사용자가 작성한 리뷰 목록 조회
	 */
	public List<Review> getReviewsByUser(User user) {
		return reviewRepository.findByUserId(user.getId());
	}

	/**
	 * 상품에 대한 리뷰 목록 조회
	 */
	public List<Review> getReviewsByProduct(Product product) {
		return reviewRepository.findByStoreId(product.getStoreId());
	}

	/**
	 * 리뷰 생성 또는 수정
	 */
	@Transactional
	public Review createReview(Review review) {
		if (review.getCreatedAt() == null) {
			review.setCreatedAt(LocalDateTime.now());
		}
		review.setUpdatedAt(LocalDateTime.now());
		return reviewRepository.save(review);
	}

	/**
	 * 리뷰 삭제
	 */
	@Transactional
	public void deleteReview(Long id) {
		reviewRepository.deleteById(id);
	}
}
