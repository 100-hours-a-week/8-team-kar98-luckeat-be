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

	@Schema(description = "가게를 소유한 회원 ID", example = "1")
	@Column(name = "user_id", columnDefinition = "BIGINT UNSIGNED")
	private Long userId;

	@Schema(description = "가게 이름", example = "맛있는 빵집")
	@Column(name = "store_name", nullable = false)
	private String storeName;

	@Schema(description = "가게 이미지 URL", example = "https://example.com/image.jpg")
	@Column(name = "store_img", columnDefinition = "TEXT")
	private String storeImg;

	@Schema(description = "가게 주소", example = "제주시 연동 123-45")
	@Column(name = "address", nullable = false)
	private String address;

	@Schema(description = "가게 공유 횟수", example = "0")
	@Column(name = "share_count", nullable = false, columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
	private Long shareCount;

	@Schema(description = "가게 상세 페이지 단축 URL", example = "{hashcode}")
	@Column(name = "store_url")
	private String storeUrl;

	@Schema(description = "리뷰 작성 권한 부여 URL", example = "https://review.url/xyz789")
	@Column(name = "permission_url")
	private String permissionUrl;

	@Schema(description = "가게 위치 위도", example = "37.5665")
	@Column(name = "latitude", nullable = false)
	private Float latitude;

	@Schema(description = "가게 위치 경도", example = "126.9780")
	@Column(name = "longitude", nullable = false)
	private Float longitude;

	@Schema(description = "가게 연락처", example = "02-1234-5678")
	@Column(name = "contact_number")
	private String contactNumber;

	@Schema(description = "가게 상세 설명", example = "맛있는 빵을 판매하는 가게입니다.")
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Schema(description = "사업자 등록 번호", example = "123-45-67890")
	@Column(name = "business_number")
	private String businessNumber;

	@Schema(description = "영업 시간", example = "매일 11:00-22:00")
	@Column(name = "business_hours", columnDefinition = "TEXT")
	private String businessHours;

	@Schema(description = "가게 웹사이트", example = "https://www.example.com")
	@Column(name = "website", columnDefinition = "TEXT")
	private String website;

	@Schema(description = "가게 평균 별점", example = "4.5")
	@Column(name = "avg_rating")
	private Float avgRating;

	@Schema(description = "구글 평균 별점", example = "4.3")
	@Column(name = "avg_rating_google")
	private Float avgRatingGoogle;

	@Schema(description = "구글 리뷰 요약", example = "친절하고 맛있는 음식점입니다.")
	@Column(name = "review_summary", columnDefinition = "TEXT")
	private String reviewSummary;

	@Schema(description = "구글 장소 ID", example = "1234567890")
	@Column(name = "google_place_id")
	private String googlePlaceId;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	@Builder.Default
	@Schema(description = "가게에서 판매하는 상품 목록")
	private List<Product> products = new ArrayList<>();

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY) // Review와의 관계 설정
	@Builder.Default
	@Schema(description = "가게에 대한 리뷰 목록")
	private List<Review> reviews = new ArrayList<>();

	@Schema(description = "픽업 가능 시간", example = "12:00-13:00, 17:00-18:00")
    @Column(name = "pickup_time", columnDefinition = "TEXT COMMENT '픽업 가능 시간'")
    private String pickupTime;

	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl;
	}
}
