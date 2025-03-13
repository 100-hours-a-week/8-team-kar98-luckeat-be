package com.luckeat.luckeatbackend.category.model;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {

	@Column(name = "category_name")
	private String categoryName;

	@Column(name = "category_image")
	private String categoryImage;

}
