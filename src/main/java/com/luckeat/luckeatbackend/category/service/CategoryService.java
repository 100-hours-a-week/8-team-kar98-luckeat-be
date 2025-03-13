package com.luckeat.luckeatbackend.category.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.category.model.Category;
import com.luckeat.luckeatbackend.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public Optional<Category> getCategoryById(Long id) {
		return categoryRepository.findById(id);
	}

	@Transactional
	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Transactional
	public void deleteCategory(Long id) {
		// categoryRepository.deleteById(id);
		// TODO: soft delete category
	}
}
