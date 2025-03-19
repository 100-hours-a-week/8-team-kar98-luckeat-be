package com.luckeat.luckeatbackend.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.common.exception.review.ReviewForbiddenException;
import com.luckeat.luckeatbackend.common.exception.review.ReviewNotFoundException;
import com.luckeat.luckeatbackend.common.exception.user.UnauthenticatedException;
import com.luckeat.luckeatbackend.common.exception.user.UserNotFoundException;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.review.dto.ReviewRequestDto;
import com.luckeat.luckeatbackend.review.dto.ReviewResponseDto;
import com.luckeat.luckeatbackend.review.dto.ReviewUpdateDto;
import com.luckeat.luckeatbackend.review.model.Review;
import com.luckeat.luckeatbackend.review.repository.ReviewRepository;
import com.luckeat.luckeatbackend.reviewpermission.service.ReviewPermissionService;
import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewPermissionService permissionService;
	private final UserService userService;

	public List<ReviewResponseDto> getAllReviews() {
		Long userId = getCurrentUserId();
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
		return reviewRepository.findByStoreIdAndDeletedAtIsNull(product.getStore().getId()).stream()
				.map(ReviewResponseDto::fromEntity).collect(Collectors.toList());
	}

	public List<ReviewResponseDto> getReviewsByStoreId(Long storeId) {
		return reviewRepository.findByStoreIdAndDeletedAtIsNull(storeId).stream().map(ReviewResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	@Transactional
	public Review createReview(ReviewRequestDto requestDto) {
		Long userId = getCurrentUserId();
		// DTO에 Bean Validation이 적용되어 있으므로 별도 검증 로직 제거

		if (!permissionService.hasPermission(userId, requestDto.getStoreId())) {
			throw new ReviewForbiddenException();
		}

		Review review = new Review();
		review.setUserId(userId);
		review.setStoreId(requestDto.getStoreId());
		review.setRating(requestDto.getRating());
		review.setReviewContent(requestDto.getReviewContent());
		review.setReviewImage(requestDto.getReviewImage());

		return reviewRepository.save(review);
	}

	@Transactional
	public Review updateReview(Long reviewId, ReviewUpdateDto updateDto) {
		Long userId = getCurrentUserId();
		// DTO에 Bean Validation이 적용되어 있으므로 별도 검증 로직 제거

		Review existingReview = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
				.orElseThrow(() -> new ReviewNotFoundException());

		if (!existingReview.getUserId().equals(userId)) {
			throw new ReviewForbiddenException();
		}

		existingReview.setRating(updateDto.getRating());
		existingReview.setReviewContent(updateDto.getReviewContent());
		if (updateDto.getReviewImage() != null) {
			existingReview.setReviewImage(updateDto.getReviewImage());
		}
		return reviewRepository.save(existingReview);
	}

	@Transactional
	public List<ReviewResponseDto> getMyReviews() {
		Long userId = getCurrentUserId();

		return reviewRepository.findByUserIdAndDeletedAtIsNull(userId).stream().map(ReviewResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	@Transactional
	public void deleteReview(Long id) {
		Long userId = getCurrentUserId();
		Review review = reviewRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new ReviewNotFoundException());

		if (!review.getUserId().equals(userId)) {
			throw new ReviewForbiddenException();
		}

		review.setDeletedAt(LocalDateTime.now());
		reviewRepository.save(review);
	}

	// 현재 인증된 사용자 ID 가져오기
	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			throw new UnauthenticatedException();
		}

		// 현재 인증된 사용자의 이메일 가져오기
		String email = authentication.getName();

		// 이메일로 사용자 ID 조회
		return userService.getUserByEmail(email)
				.orElseThrow(() -> new UserNotFoundException()).getId();
	}
}
