package com.luckeat.luckeatbackend.store.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.review.model.Review;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "가게 엔티티")
public class Store extends BaseEntity {

	@Column(name = "user_id", nullable = false)
	@Schema(description = "가게 소유자 ID", example = "100")
	private Long userId;

	@Column(name = "category_id", nullable = false)
	@Schema(description = "카테고리 ID", example = "2")
	private Long categoryId;

	@Column(name = "store_name", nullable = false, length = 255)
	@Schema(description = "가게 이름", example = "맛있는 국수집")
	private String storeName;

	@Column(name = "store_img", nullable = false, columnDefinition = "TEXT")
	@Schema(description = "가게 이미지 URL", example = "https://example.com/image.jpg")
	private String storeImg;

	@Column(name = "address", nullable = false, length = 255)
	@Schema(description = "가게 주소", example = "서울시 강남구 역삼동 123-45")
	private String address;

	@Column(name = "store_url", length = 255)
	@Schema(description = "가게 웹사이트 URL", example = "https://example.com/store")
	private String storeUrl;

	@Column(name = "share_count", nullable = false)
	@Builder.Default
	@Schema(description = "공유 횟수", example = "42")
	private Long shareCount = 0L;

	@Column(name = "permission_url", length = 255)
	@Schema(description = "가게 허가증 URL", example = "https://example.com/permission.jpg")
	private String permissionUrl;

	@Column(name = "latitude", nullable = false)
	@Schema(description = "위도", example = "37.123456")
	private Double latitude;

	@Column(name = "longitude", nullable = false)
	@Schema(description = "경도", example = "127.123456")
	private Double longitude;

	@Column(name = "contact_number", length = 255)
	@Schema(description = "연락처", example = "02-1234-5678")
	private String contactNumber;

	@Column(name = "description", columnDefinition = "TEXT")
	@Schema(description = "가게 설명", example = "맛있는 국수와 다양한 반찬을 제공하는 가게입니다.")
	private String description;

	@Column(name = "business_number", length = 255)
	@Schema(description = "사업자 번호", example = "123-45-67890")
	private String businessNumber;

	@Column(name = "weekday_close_time")
	@Schema(description = "평일 마감 시간", example = "22:00")
	private LocalTime weekdayCloseTime;

	@Column(name = "weekend_close_time")
	@Schema(description = "주말 마감 시간", example = "23:00")
	private LocalTime weekendCloseTime;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	@Builder.Default
	@Schema(description = "가게에서 판매하는 상품 목록")
	private List<Product> products = new ArrayList<>();

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY) // Review와의 관계 설정
	@Builder.Default
	@Schema(description = "가게에 대한 리뷰 목록")
	private List<Review> reviews = new ArrayList<>();

}
