package com.luckeat.luckeatbackend.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.review.dto.ReviewRequestDto;
import com.luckeat.luckeatbackend.review.dto.ReviewResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewUpdateDto;
import com.luckeat.luckeatbackend.review.model.Review;
import com.luckeat.luckeatbackend.review.repository.ReviewRepository;
import com.luckeat.luckeatbackend.users.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

	private final ReviewRepository reviewRepository;

	public List<ReviewResponseDto> getAllReviews() {
		return reviewRepository.findByDeletedAtIsNull().stream().map(ReviewResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	// 소프트 삭제 적용 - 삭제된 리뷰 제외
	public Optional<Review> getReviewById(Long id) {
		return reviewRepository.findByIdAndDeletedAtIsNull(id);
	}

	// 소프트 삭제 적용 - 삭제된 리뷰 제외
	public Optional<ReviewResponseDto> getReviewDtoById(Long id) {
		return reviewRepository.findByIdAndDeletedAtIsNull(id).map(ReviewResponseDto::fromEntity);
	}

	public List<ReviewResponseDto> getReviewsByUser(User user) {
		return reviewRepository.findByUserIdAndDeletedAtIsNull(user.getId()).stream().map(ReviewResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	public List<ReviewResponseDto> getReviewsByUserId(Long userId) {
		return reviewRepository.findByUserIdAndDeletedAtIsNull(userId).stream().map(ReviewResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	public List<ReviewResponseDto> getReviewsByProduct(Product product) {
		return reviewRepository.findByStoreIdAndDeletedAtIsNull(product.getStoreId()).stream()
				.map(ReviewResponseDto::fromEntity).collect(Collectors.toList());
	}

	public List<ReviewResponseDto> getReviewsByStoreId(Long storeId) {
		return reviewRepository.findByStoreIdAndDeletedAtIsNull(storeId).stream().map(ReviewResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	@Transactional
	public Review createReview(ReviewRequestDto requestDto, Long userId) {
		validateReviewRequest(requestDto);

		Review review = new Review();
		review.setUserId(userId);
		review.setStoreId(requestDto.getStoreId());
		review.setRating(requestDto.getRating());
		review.setReviewContent(requestDto.getReviewContent());
		review.setReviewImage(requestDto.getReviewImage());

		return reviewRepository.save(review);
	}

	@Transactional
	public Review updateReview(Long reviewId, ReviewUpdateDto updateDto, Long userId) {
		validateReviewUpdate(updateDto);

		Review existingReview = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
				.orElseThrow(() -> new IllegalStateException("리뷰를 찾을 수 없습니다: " + reviewId));

		if (!existingReview.getUserId().equals(userId)) {
			throw new IllegalStateException("이 리뷰를 수정할 권한이 없습니다");
		}

		existingReview.setRating(updateDto.getRating());
		existingReview.setReviewContent(updateDto.getReviewContent());
		if (updateDto.getReviewImage() != null) {
			existingReview.setReviewImage(updateDto.getReviewImage());
		}
		return reviewRepository.save(existingReview);
	}

	@Transactional
	public void deleteReview(Long id, Long userId) {
		Review review = reviewRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new IllegalStateException("리뷰를 찾을 수 없습니다: " + id));

		if (!review.getUserId().equals(userId)) {
			throw new IllegalStateException("이 리뷰를 삭제할 권한이 없습니다");
		}

		review.setDeletedAt(LocalDateTime.now());
		reviewRepository.save(review);
	}

	private void validateReviewRequest(ReviewRequestDto requestDto) {
		if (requestDto.getStoreId() == null) {
			throw new IllegalArgumentException("스토어 ID는 필수입니다");
		}

		if (requestDto.getRating() == null) {
			throw new IllegalArgumentException("평점은 필수입니다");
		}

		if (requestDto.getRating() < 1 || requestDto.getRating() > 5) {
			throw new IllegalArgumentException("평점은 1~5 사이여야 합니다");
		}

		if (requestDto.getReviewContent() == null || requestDto.getReviewContent().trim().length() < 5) {
			throw new IllegalArgumentException("리뷰 내용은 5글자 이상이어야 합니다");
		}
	}

	private void validateReviewUpdate(ReviewUpdateDto updateDto) {
		if (updateDto.getRating() == null) {
			throw new IllegalArgumentException("평점은 필수입니다");
		}

		if (updateDto.getRating() < 1 || updateDto.getRating() > 5) {
			throw new IllegalArgumentException("평점은 1~5 사이여야 합니다");
		}

		if (updateDto.getReviewContent() == null || updateDto.getReviewContent().trim().length() < 5) {
			throw new IllegalArgumentException("리뷰 내용은 5글자 이상이어야 합니다");
		}
	}
}
