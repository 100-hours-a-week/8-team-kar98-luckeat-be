package com.luckeat.luckeatbackend.review.model;

import java.time.LocalDateTime;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.users.model.User;

import jakarta.persistence.*;
import jakarta.persistence.metamodel.IdentifiableType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "store_id", nullable = false)
	private Long storeId;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "review_content", nullable = false, columnDefinition = "TEXT")
	private String reviewContent;

	@Column(name = "review_image", columnDefinition = "TEXT")
	private String reviewImage;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
