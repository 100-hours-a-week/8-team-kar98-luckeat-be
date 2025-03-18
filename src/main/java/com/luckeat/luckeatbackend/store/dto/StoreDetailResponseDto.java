package com.luckeat.luckeatbackend.store.dto;

import java.time.LocalTime;
import java.util.List;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.store.model.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가게 상세 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDetailResponseDto {
	private Long id;
	private Long userId;
	private Long categoryId;
	private String storeName;
	private String storeImg;
	private String address;
	private String storeUrl;
	private Long shareCount;
	private String permissionUrl;
	private Double latitude;
	private Double longitude;
	private String contactNumber;
	private String description;
	private String businessNumber;
	private LocalTime weekdayCloseTime;
	private LocalTime weekendCloseTime;
	private List<ProductResponseDto> products;

	public static StoreDetailResponseDto fromEntity(Store store, List<Product> products) {
		return StoreDetailResponseDto.builder().id(store.getId()).userId(store.getUserId())
				.categoryId(store.getCategoryId()).storeName(store.getStoreName()).storeImg(store.getStoreImg())
				.address(store.getAddress()).storeUrl(store.getStoreUrl()).shareCount(store.getShareCount())
				.permissionUrl(store.getPermissionUrl()).latitude(store.getLatitude()).longitude(store.getLongitude())
				.contactNumber(store.getContactNumber()).description(store.getDescription())
				.businessNumber(store.getBusinessNumber()).weekdayCloseTime(store.getWeekdayCloseTime())
				.weekendCloseTime(store.getWeekendCloseTime())
				.products(products.stream().map(ProductResponseDto::fromEntity).toList()).build();
	}
}