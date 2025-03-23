package com.luckeat.luckeatbackend.store.dto;

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
public class StoreResponseDto {
	@Schema(description = "가게 ID", example = "1")
	private Long id;
	
	@Schema(description = "가게 소유자 ID", example = "100")
	private Long userId;
	
	@Schema(description = "카테고리 ID", example = "2")
	private Long categoryId;
	
	@Schema(description = "가게 이름", example = "맛있는 국수집")
	private String storeName;
	
	@Schema(description = "가게 이미지 URL", example = "https://example.com/image.jpg")
	private String storeImg;
	
	@Schema(description = "가게 주소", example = "서울시 강남구 역삼동 123-45")
	private String address;
	
	@Schema(description = "가게 웹사이트 URL", example = "https://example.com/store")
	private String storeUrl;
	
	@Schema(description = "공유 횟수", example = "42")
	private Long shareCount;

	@Schema(description = "가게 평점", example = "4.5")
	private Double averageRating;

	@Schema(description = "리뷰 수", example = "10")
	private Long reviewCount;

	@Schema(description = "가게 허가증 URL", example = "https://example.com/permission.jpg")
	private String permissionUrl;
	
	@Schema(description = "위도", example = "37.123456")
	private Double latitude;
	
	@Schema(description = "경도", example = "127.123456")
	private Double longitude;
	
	@Schema(description = "연락처", example = "02-1234-5678")
	private String contactNumber;
	
	@Schema(description = "가게 설명", example = "맛있는 국수와 다양한 반찬을 제공하는 가게입니다.")
	private String description;
	
	@Schema(description = "사업자 번호", example = "123-45-67890")
	private String businessNumber;
	
	@Schema(description = "평일 마감 시간", example = "22:00")
	private LocalTime weekdayCloseTime;
	
	@Schema(description = "주말 마감 시간", example = "23:00")
	private LocalTime weekendCloseTime;

	public static StoreResponseDto fromEntity(Store store) {
		return StoreResponseDto.builder().id(store.getId()).userId(store.getUserId()).categoryId(store.getCategoryId())
				.storeName(store.getStoreName()).storeImg(store.getStoreImg()).address(store.getAddress())
				.storeUrl(store.getStoreUrl()).shareCount(store.getShareCount()).permissionUrl(store.getPermissionUrl())
				.latitude(store.getLatitude()).longitude(store.getLongitude()).contactNumber(store.getContactNumber())
				.description(store.getDescription()).businessNumber(store.getBusinessNumber())
				.weekdayCloseTime(store.getWeekdayCloseTime()).weekendCloseTime(store.getWeekendCloseTime())
				.averageRating(store.getAverageRating())
				.reviewCount(store.getReviews().stream()
                .filter(review -> review.getDeletedAt() == null) 
                .count()) 
				.build();
	}
}