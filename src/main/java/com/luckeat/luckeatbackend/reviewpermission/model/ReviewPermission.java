package com.luckeat.luckeatbackend.reviewpermission.model;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPermission extends BaseEntity {

	@Column(name = "store_id", nullable = false)
	private Long storeId;

	@Column(name = "user_id", nullable = false)
	private Long userId;
}
