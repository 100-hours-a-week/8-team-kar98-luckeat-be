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

public class StoreDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Request {
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
					.businessNumber(businessNumber).weekdayCloseTime(weekdayCloseTime)
					.weekendCloseTime(weekendCloseTime).build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
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

		public static Response fromEntity(Store store) {
			return Response.builder().id(store.getId()).userId(store.getUserId()).categoryId(store.getCategoryId())
					.storeName(store.getStoreName()).storeImg(store.getStoreImg()).address(store.getAddress())
					.storeUrl(store.getStoreUrl()).shareCount(store.getShareCount())
					.permissionUrl(store.getPermissionUrl()).latitude(store.getLatitude())
					.longitude(store.getLongitude()).contactNumber(store.getContactNumber())
					.description(store.getDescription()).businessNumber(store.getBusinessNumber())
					.weekdayCloseTime(store.getWeekdayCloseTime()).weekendCloseTime(store.getWeekendCloseTime())
					.build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class DetailResponse {
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
		private List<ProductResponse> products;

		public static DetailResponse fromEntity(Store store, List<Product> products) {
			return DetailResponse.builder().id(store.getId()).userId(store.getUserId())
					.categoryId(store.getCategoryId()).storeName(store.getStoreName()).storeImg(store.getStoreImg())
					.address(store.getAddress()).storeUrl(store.getStoreUrl()).shareCount(store.getShareCount())
					.permissionUrl(store.getPermissionUrl()).latitude(store.getLatitude())
					.longitude(store.getLongitude()).contactNumber(store.getContactNumber())
					.description(store.getDescription()).businessNumber(store.getBusinessNumber())
					.weekdayCloseTime(store.getWeekdayCloseTime()).weekendCloseTime(store.getWeekendCloseTime())
					.products(products.stream().map(ProductResponse::fromEntity).toList()).build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProductResponse {
		private Long id;
		private String productName;
		private String productImg;
		private Long originalPrice;
		private Long discountedPrice;
		private Boolean isOpen;

		public static ProductResponse fromEntity(Product product) {
			return ProductResponse.builder().id(product.getId()).productName(product.getProductName())
					.productImg(product.getProductImg()).originalPrice(product.getOriginalPrice())
					.discountedPrice(product.getDiscountedPrice()).isOpen(product.getIsOpen()).build();
		}
	}
}