package com.luckeat.luckeatbackend.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.review.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	// 삭제되지 않은 모든 리뷰 조회
	List<Review> findByDeletedAtIsNull();

	// ID로 삭제되지 않은 리뷰 조회
	Optional<Review> findByIdAndDeletedAtIsNull(Long id);

	// 특정 사용자의 삭제되지 않은 리뷰 조회
	List<Review> findByUserIdAndDeletedAtIsNull(Long userId);

	// 특정 상점의 삭제되지 않은 리뷰 조회
	List<Review> findByStoreIdAndDeletedAtIsNull(Long storeId);


}
