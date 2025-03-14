package com.luckeat.luckeatbackend.permission.model;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review_permissions")
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
