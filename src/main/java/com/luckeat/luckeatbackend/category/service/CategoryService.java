package com.luckeat.luckeatbackend.category.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.category.model.Category;
import com.luckeat.luckeatbackend.category.repository.CategoryRepository;
import com.luckeat.luckeatbackend.common.exception.category.CategoryInvalidImageException;
import com.luckeat.luckeatbackend.common.exception.category.CategoryInvalidNameException;
import com.luckeat.luckeatbackend.common.exception.category.CategoryNameDuplicateException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public List<Category> getAllCategories() {
		// 삭제된 카테고리는 보여주지 않도록 수정
		return categoryRepository.findByDeletedAtIsNull();
	}

	public Optional<Category> getCategoryById(Long id) {
		return categoryRepository.findById(id);
	}

	@Transactional
	public Category saveCategory(Category category) {
		// 카테고리 이름 중복 검사 추가
		if (categoryRepository.existsByCategoryNameAndDeletedAtIsNull(category.getCategoryName())) {
			throw new CategoryNameDuplicateException();
		}

		// 카테고리 이름 유효성 검사 추가
		validateCategoryName(category.getCategoryName());

		// 카테고리 이미지 유효성 검사 추가
		validateCategoryImage(category.getCategoryImage());

		return categoryRepository.save(category);
	}

	private void validateCategoryName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new CategoryInvalidNameException();
		}

		if (name.length() < 1 || name.length() > 10) {
			throw new CategoryInvalidNameException();
		}

		if (!name.matches("^[가-힣]+$")) {
			throw new CategoryInvalidNameException();
		}
	}

	private void validateCategoryImage(String imageUrl) {
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			throw new CategoryInvalidImageException();
		}

		if (!imageUrl.matches("^(http|https)://.*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
			throw new CategoryInvalidImageException();
		}
	}
}
