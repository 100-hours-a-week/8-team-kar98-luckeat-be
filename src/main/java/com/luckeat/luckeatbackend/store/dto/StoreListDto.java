package com.luckeat.luckeatbackend.store.dto;

import java.io.Serializable;

import com.luckeat.luckeatbackend.store.model.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가게 목록 응답 DTO (간소화 버전)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가게 목록 응답 DTO")
public class StoreListDto implements Serializable {
	private static final long serialVersionUID = 1L; // Serializable 구현

	@Schema(description = "가게 ID", example = "1")
	private Long id;

	@Schema(description = "가게 이름", example = "맛있는 국수집")
	private String storeName;

	@Schema(description = "가게 이미지 URL", example = "https://example.com/image.jpg")
	private String storeImg;

	@Schema(description = "가게 주소", example = "서울시 강남구 역삼동 123-45")
	private String address;

	@Schema(description = "구글 평균 별점", example = "4.3")
	private Float avgRatingGoogle;

	@Schema(description = "공유 횟수", example = "42")
	private Long shareCount;

	@Schema(description = "위도", example = "37.123456")
	private Float latitude;

	@Schema(description = "경도", example = "127.123456")
	private Float longitude;

	@Schema(description = "리뷰 수", example = "15")
	private Long reviewCount;

	// 엔티티에서 필요한 필드만 가져와 DTO 생성 (Lazy Loading 필드 접근 X)
	public static StoreListDto fromEntity(Store store) {
		return StoreListDto.builder()
				.id(store.getId())
				.storeName(store.getStoreName())
				.storeImg(store.getStoreImg())
				.address(store.getAddress())
				.avgRatingGoogle(store.getAvgRatingGoogle())
				.shareCount(store.getShareCount())
				.latitude(store.getLatitude())
				.longitude(store.getLongitude())
				.build();
	}
} 