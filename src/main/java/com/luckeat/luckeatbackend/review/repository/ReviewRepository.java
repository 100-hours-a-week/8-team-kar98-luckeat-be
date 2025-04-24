package com.luckeat.luckeatbackend.review.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	// 특정 예약의 리뷰 조회
	Optional<Review> findByReservationId(Long reservationId);

	// 여러 Store ID에 대한 리뷰 수를 조회하는 메서드 추가
	@Query("SELECT r.store.id as storeId, COUNT(r.id) as reviewCount " +
		   "FROM Review r " +
		   "WHERE r.store.id IN :storeIds AND r.deletedAt IS NULL " +
		   "GROUP BY r.store.id")
	List<Map<String, Object>> findReviewCountsByStoreIds(@Param("storeIds") List<Long> storeIds);

	// 특정 사용자의 리뷰 수를 계산하는 메서드 (필요 시 사용)
	long countByUserIdAndDeletedAtIsNull(Long userId);

	// 특정 가게의 리뷰 수를 계산하는 메서드 (N+1 문제 가능성 있으므로 주의)
	long countByStoreIdAndDeletedAtIsNull(Long storeId);
}
