package com.luckeat.luckeatbackend.category.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.category.model.Category;
import com.luckeat.luckeatbackend.category.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

    // 모든 카테고리 가져오기
	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}

    // 카테고리 아이디로 카테고리 가져오기
	@GetMapping("/{category_id}")
	public ResponseEntity<Category> getCategoryById(@PathVariable Long categoryId) {
		return categoryService.getCategoryById(categoryId).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

    // 카테고리 생성
	@PostMapping
	public ResponseEntity<Category> createCategory(@RequestBody Category category) {
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(category));
	}

    // 카테고리 수정
	@PutMapping("/{category_id}")
	public ResponseEntity<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
		return categoryService.getCategoryById(categoryId).map(existingCategory -> {
			category.setId(categoryId);
			return ResponseEntity.ok(categoryService.saveCategory(category));
		}).orElse(ResponseEntity.notFound().build());
	}

    // 카테고리 삭제
	@DeleteMapping("/{category_id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
		return categoryService.getCategoryById(categoryId).map(existingCategory -> {
			existingCategory.setDeletedAt(LocalDateTime.now());
			categoryService.saveCategory(existingCategory);
			return ResponseEntity.noContent().<Void>build();
		}).orElse(ResponseEntity.notFound().build());
	}
}
