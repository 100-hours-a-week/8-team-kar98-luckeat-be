package com.luckeat.luckeatbackend.store.dto;

import java.io.Serializable;
import java.time.LocalTime;

import com.luckeat.luckeatbackend.store.model.Store;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가게 기본 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가게 기본 응답 DTO")
public class StoreResponseDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "가게 ID", example = "1")
	private Long id;
	
	@Schema(description = "가게 소유자 ID", example = "100")
	private Long userId;

	@Schema(description = "카테고리 ID", example = "1")
	private Long categoryId;
	
	@Schema(description = "가게 이름", example = "맛있는 국수집")
	private String storeName;
	
	@Schema(description = "가게 이미지 URL", example = "https://example.com/image.jpg")
	private String storeImg;
	
	@Schema(description = "가게 주소", example = "서울시 강남구 역삼동 123-45")
	private String address;
	
	@Schema(description = "가게 웹사이트", example = "https://example.com/store")
	private String website;
	
	@Schema(description = "가게 상세 페이지 URL", example = "https://short.url/abc123")
	private String storeUrl;
	
	@Schema(description = "공유 횟수", example = "42")
	private Long shareCount;

	@Schema(description = "가게 평균 별점", example = "4.5")
	private Float avgRating;

	@Schema(description = "구글 평균 별점", example = "4.3")
	private Float avgRatingGoogle;

	@Schema(description = "리뷰 요약", example = "친절하고 맛있는 음식점입니다.")
	private String reviewSummary;
	
	@Schema(description = "위도", example = "37.123456")
	private Float latitude;
	
	@Schema(description = "경도", example = "127.123456")
	private Float longitude;
	
	@Schema(description = "연락처", example = "02-1234-5678")
	private String contactNumber;
	
	@Schema(description = "가게 설명", example = "맛있는 국수와 다양한 반찬을 제공하는 가게입니다.")
	private String description;
	
	@Schema(description = "사업자 번호", example = "123-45-67890")
	private String businessNumber;
	
	@Schema(description = "영업 시간", example = "매일 11:00-22:00")
	private String businessHours;

	@Schema(description = "픽업 가능 시간", example = "12:00-13:00, 17:00-18:00")
	private String pickupTime;

	@Schema(description = "리뷰 수", example = "10")
	private Long reviewCount;

	public static StoreResponseDto fromEntity(Store store) {
		return StoreResponseDto.builder()
				.id(store.getId())
				.userId(store.getUserId())
				.categoryId(store.getCategoryId())
				.storeName(store.getStoreName())
				.storeImg(store.getStoreImg())
				.address(store.getAddress())
				.website(store.getWebsite())
				.storeUrl(store.getStoreUrl())
				.shareCount(store.getShareCount())
				.avgRating(store.getAvgRating())
				.avgRatingGoogle(store.getAvgRatingGoogle())
				.reviewSummary(store.getReviewSummary())
				.latitude(store.getLatitude())
				.longitude(store.getLongitude())
				.contactNumber(store.getContactNumber())
				.description(store.getDescription())
				.businessNumber(store.getBusinessNumber())
				.businessHours(store.getBusinessHours())
				.pickupTime(store.getPickupTime())
				.reviewCount(store.getReviews() != null ? 
					store.getReviews().stream()
						.filter(review -> review.getDeletedAt() == null)
						.count() : 0L)
				.build();
	}
}