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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 API", description = "카테고리 관련 API 목록")
public class CategoryController {

	private final CategoryService categoryService;

	@Operation(summary = "모든 카테고리 조회", description = "시스템에 등록된 모든 카테고리를, 반환합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}

	@Operation(summary = "카테고리 상세 조회", description = "특정 ID의 카테고리 정보를 조회합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "카테고리 조회 성공"),
		@ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
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
