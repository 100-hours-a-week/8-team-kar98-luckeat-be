package com.luckeat.luckeatbackend.category.model;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리 모델")
public class Category extends BaseEntity {

	@Schema(description = "카테고리 ID", example = "1")
	@Column(name = "category_name")
	private String categoryName;

	@Schema(description = "카테고리 이름", example = "한식")
	@Column(name = "category_image")
	private String categoryImage;

	@Schema(description = "카테고리 설명", example = "한국 전통 음식")
	private String description;

}
