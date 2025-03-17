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
import com.luckeat.luckeatbackend.common.exception.category.CategoryInvalidImageException;
import com.luckeat.luckeatbackend.common.exception.category.CategoryInvalidNameException;
import com.luckeat.luckeatbackend.common.exception.category.CategoryNameDuplicateException;
import com.luckeat.luckeatbackend.common.exception.category.CategoryNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
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
	public ResponseEntity<Category> getCategoryById(@PathVariable("category_id") Long categoryId) {
		return ResponseEntity
				.ok(categoryService.getCategoryById(categoryId).orElseThrow(() -> new CategoryNotFoundException()));
	}

	// 카테고리 생성
	@PostMapping
	public ResponseEntity<Category> createCategory(@RequestBody Category category) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(category));
		} catch (CategoryNameDuplicateException | CategoryInvalidNameException | CategoryInvalidImageException e) {
			log.warn("카테고리 생성 중 검증 오류 발생: {}", e.getMessage());
			throw e;
		}
	}

	// 카테고리 수정
	@PutMapping("/{category_id}")
	public ResponseEntity<Category> updateCategory(@PathVariable("category_id") Long categoryId,
			@RequestBody Category category) {
		// 먼저 카테고리 존재 여부 확인
		categoryService.getCategoryById(categoryId).orElseThrow(() -> new CategoryNotFoundException());

		category.setId(categoryId);
		try {
			return ResponseEntity.ok(categoryService.saveCategory(category));
		} catch (CategoryNameDuplicateException | CategoryInvalidNameException | CategoryInvalidImageException e) {
			log.warn("카테고리 수정 중 검증 오류 발생: {}", e.getMessage());
			throw e;
		}
	}

	// 카테고리 삭제
	@DeleteMapping("/{category_id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable("category_id") Long categoryId) {
		Category existingCategory = categoryService.getCategoryById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException());

		existingCategory.setDeletedAt(LocalDateTime.now());
		categoryService.saveCategory(existingCategory);
		return ResponseEntity.noContent().build();
	}
}
