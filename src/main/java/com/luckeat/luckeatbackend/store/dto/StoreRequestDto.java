package com.luckeat.luckeatbackend.store.dto;

import java.time.LocalTime;

import com.luckeat.luckeatbackend.store.model.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 가게 등록/수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRequestDto {
	private Long categoryId;
	private String storeName;
	private String storeImg;
	private String address;
	private String storeUrl;
	private String permissionUrl;
	private Double latitude;
	private Double longitude;
	private String contactNumber;
	private String description;
	private String businessNumber;
	private LocalTime weekdayCloseTime;
	private LocalTime weekendCloseTime;

	public Store toEntity(Long userId) {
		return Store.builder().userId(userId).categoryId(categoryId).storeName(storeName).storeImg(storeImg)
				.address(address).storeUrl(storeUrl).permissionUrl(permissionUrl).latitude(latitude)
				.longitude(longitude).contactNumber(contactNumber).description(description)
				.businessNumber(businessNumber).weekdayCloseTime(weekdayCloseTime).weekendCloseTime(weekendCloseTime)
				.build();
	}
}