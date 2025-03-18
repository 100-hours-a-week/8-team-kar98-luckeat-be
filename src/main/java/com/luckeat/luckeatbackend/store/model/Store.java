package com.luckeat.luckeatbackend.store.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;
import com.luckeat.luckeatbackend.product.model.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "category_id", nullable = false)
	private Long categoryId;

	@Column(name = "store_name", nullable = false, length = 255)
	private String storeName;

	@Column(name = "store_img", nullable = false, columnDefinition = "TEXT")
	private String storeImg;

	@Column(name = "address", nullable = false, length = 255)
	private String address;

	@Column(name = "store_url", length = 255)
	private String storeUrl;

	@Column(name = "share_count", nullable = false)
	@Builder.Default
	private Long shareCount = 0L;

	@Column(name = "permission_url", length = 255)
	private String permissionUrl;

	@Column(name = "latitude", nullable = false)
	private Double latitude;

	@Column(name = "longitude", nullable = false)
	private Double longitude;

	@Column(name = "contact_number", length = 255)
	private String contactNumber;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "business_number", length = 255)
	private String businessNumber;

	@Column(name = "weekday_close_time")
	private LocalTime weekdayCloseTime;

	@Column(name = "weekend_close_time")
	private LocalTime weekendCloseTime;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	@Builder.Default
	private List<Product> products = new ArrayList<>();

}
