package com.luckeat.luckeatbackend.store.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luckeat.luckeatbackend.store.model.Store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가게, 등록/수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가게 등록/수정 요청 DTO")
public class StoreRequestDto {
	@NotNull(message = "카테고리 ID는 필수 항목입니다")
	@Schema(description = "카테고리 ID", example = "1", required = true)
	private Long categoryId;

	@NotBlank(message = "가게 이름은 필수 항목입니다")
	@Size(min = 1, max = 255, message = "가게 이름은 1-255자 사이여야 합니다")
	@Schema(description = "가게 이름", example = "맛있는 국수집", required = true)
	private String storeName;
	
	@NotBlank(message = "가게 이미지는 필수 항목입니다")
	@Schema(description = "가게 이미지 URL", example = "https://example.com/image.jpg", required = true)
	private String storeImg;
	
	@NotBlank(message = "주소는 필수 항목입니다")
	@Size(min = 5, max = 255, message = "주소는 5-255자 사이여야 합니다")
	@Schema(description = "가게 주소", example = "서울시 강남구 역삼동 123-45", required = true)
	private String address;
	

	@Schema(description = "가게 웹사이트", example = "https://example.com/store")
	private String website;
	

	@Schema(description = "가게 상세 페이지 URL", example = "https://short.url/abc123")
	private String storeUrl;
	

	@Schema(description = "리뷰 작성 권한 URL", example = "https://review.url/xyz789")
	private String permissionUrl;
	
	@NotNull(message = "위도는 필수 항목입니다")
	@DecimalMin(value = "-90.0", inclusive = true, message = "위도는 -90.0 이상이어야 합니다")
	@DecimalMax(value = "90.0", inclusive = true, message = "위도는 90.0 이하여야 합니다")
	@Digits(integer = 2, fraction = 6, message = "위도는 소수점 6자리까지 허용됩니다")
	@Schema(description = "위도", example = "37.123456", required = true)
	private Float latitude;
	
	@NotNull(message = "경도는 필수 항목입니다")
	@DecimalMin(value = "-180.0", inclusive = true, message = "경도는 -180.0 이상이어야 합니다")
	@DecimalMax(value = "180.0", inclusive = true, message = "경도는 180.0 이하여야 합니다")
	@Digits(integer = 3, fraction = 6, message = "경도는 소수점 6자리까지 허용됩니다")
	@Schema(description = "경도", example = "127.123456", required = true)
	private Float longitude;
	
	@Pattern(regexp = "^(0\\d{1,2}-\\d{3,4}-\\d{4})|^$", message = "전화번호는 0XX-XXXX-XXXX 또는 0X-XXXX-XXXX 형식이어야 합니다")
	@Schema(description = "연락처", example = "02-1234-5678")
	private String contactNumber;
	
	@Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
	@Schema(description = "가게 설명", example = "맛있는 국수와 다양한 반찬을 제공하는 가게입니다.")
	private String description;
	
	@Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}|^$", message = "사업자 번호는 XXX-XX-XXXXX 형식이어야 합니다")
	@Schema(description = "사업자 번호", example = "123-45-67890")
	private String businessNumber;
	
	@Size(max = 1000, message = "영업시간은 1000자 이하여야 합니다")
	@Schema(description = "영업 시간", example = "매일 11:00-22:00")
	private String businessHours;

	@Size(max = 1000, message = "리뷰 요약은 1000자 이하여야 합니다")
	@Schema(description = "리뷰 요약", example = "친절한 서비스와 맛있는 음식")
	private String reviewSummary;


	@Schema(description = "가게 평균 별점", example = "4.5")
	private Float avgRating;

	@Schema(description = "구글 평균 별점", example = "4.3")
	private Float avgRatingGoogle;

	@Schema(description = "구글 장소 ID", example = "ChIJN1t_tDeuEmsRUsoyG83frY4")
	private String googlePlaceId;


	public Store toEntity(Long userId) {
		return Store.builder()
				.userId(userId)
				.storeName(storeName)
				.categoryId(categoryId)
				.storeImg(storeImg)
				.address(address)
				.website(website)
				.storeUrl(storeUrl)
				.permissionUrl(permissionUrl)
				.latitude(latitude)
				.longitude(longitude)
				.contactNumber(contactNumber)
				.description(description)
				.businessNumber(businessNumber)
				.businessHours(businessHours)
				.reviewSummary(reviewSummary)
				.avgRating(avgRating)
				.avgRatingGoogle(avgRatingGoogle)
				.googlePlaceId(googlePlaceId)
				.shareCount(0L) // 초기값 설정
				.build();
	}
}