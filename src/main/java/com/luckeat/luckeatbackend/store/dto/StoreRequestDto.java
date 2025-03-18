package com.luckeat.luckeatbackend.store.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luckeat.luckeatbackend.store.model.Store;

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
 * 가게 등록/수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRequestDto {
	@NotNull(message = "카테고리 ID는 필수 항목입니다")
	private Long categoryId;
	
	@NotBlank(message = "가게 이름은 필수 항목입니다")
	@Size(min = 1, max = 255, message = "가게 이름은 1-255자 사이여야 합니다")
	private String storeName;
	
	@NotBlank(message = "가게 이미지는 필수 항목입니다")
	@Pattern(regexp = "^(https?://)(.*)", message = "이미지 URL은 http:// 또는 https://로 시작해야 합니다")
	private String storeImg;
	
	@NotBlank(message = "주소는 필수 항목입니다")
	@Size(min = 5, max = 255, message = "주소는 5-255자 사이여야 합니다")
	private String address;
	
	@Pattern(regexp = "^(https?://)(.*)|^$", message = "URL은 http:// 또는 https://로 시작해야 합니다")
	private String storeUrl;
	
	@Pattern(regexp = "^(https?://)(.*)|^$", message = "URL은 http:// 또는 https://로 시작해야 합니다")
	private String permissionUrl;
	
	@NotNull(message = "위도는 필수 항목입니다")
	@DecimalMin(value = "-90.0", inclusive = true, message = "위도는 -90.0 이상이어야 합니다")
	@DecimalMax(value = "90.0", inclusive = true, message = "위도는 90.0 이하여야 합니다")
	@Digits(integer = 2, fraction = 6, message = "위도는 소수점 6자리까지 허용됩니다")
	private Double latitude;
	
	@NotNull(message = "경도는 필수 항목입니다")
	@DecimalMin(value = "-180.0", inclusive = true, message = "경도는 -180.0 이상이어야 합니다")
	@DecimalMax(value = "180.0", inclusive = true, message = "경도는 180.0 이하여야 합니다")
	@Digits(integer = 3, fraction = 6, message = "경도는 소수점 6자리까지 허용됩니다")
	private Double longitude;
	
	@Pattern(regexp = "^(0\\d{1,2}-\\d{3,4}-\\d{4})|^$", message = "전화번호는 0XX-XXXX-XXXX 또는 0X-XXXX-XXXX 형식이어야 합니다")
	private String contactNumber;
	
	@Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
	private String description;
	
	@Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}|^$", message = "사업자 번호는 XXX-XX-XXXXX 형식이어야 합니다")
	private String businessNumber;
	
	@JsonFormat(pattern = "HH:mm")
	private LocalTime weekdayCloseTime;
	
	@JsonFormat(pattern = "HH:mm")
	private LocalTime weekendCloseTime;

	public Store toEntity(Long userId) {
		return Store.builder().userId(userId).categoryId(categoryId).storeName(storeName).storeImg(storeImg)
				.address(address).storeUrl(storeUrl).permissionUrl(permissionUrl).latitude(latitude)
				.longitude(longitude).contactNumber(contactNumber).description(description)
				.businessNumber(businessNumber).weekdayCloseTime(weekdayCloseTime).weekendCloseTime(weekendCloseTime)
				.build();
	}
}