package com.luckeat.luckeatbackend.category.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luckeat.luckeatbackend.category.model.Category;
import com.luckeat.luckeatbackend.category.service.CategoryService;
import com.luckeat.luckeatbackend.common.exception.category.CategoryInvalidImageException;
import com.luckeat.luckeatbackend.common.exception.category.CategoryInvalidNameException;
import com.luckeat.luckeatbackend.common.exception.category.CategoryNameDuplicateException;
import com.luckeat.luckeatbackend.common.exception.category.CategoryNotFoundException;

@Disabled("컨트롤러 테스트 설정 문제 해결 전까지 비활성화")
@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("모든 카테고리를 조회할 수 있다")
    void getAllCategories() throws Exception {
        // given
        Category category1 = new Category();
        category1.setCategoryName("한식");
        category1.setCategoryImage("https://example.com/image1.jpg");

        Category category2 = new Category();
        category2.setCategoryName("양식");
        category2.setCategoryImage("https://example.com/image2.jpg");

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(category1, category2));

        // when & then
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("한식"))
                .andExpect(jsonPath("$[1].categoryName").value("양식"));
    }

    @Test
    @DisplayName("ID로 카테고리를 조회할 수 있다")
    void getCategoryById() throws Exception {
        // given
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("https://example.com/image.jpg");

        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.of(category));

        // when & then
        mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("한식"))
                .andExpect(jsonPath("$.categoryImage").value("https://example.com/image.jpg"));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 카테고리를 조회하면 404 응답을 반환한다")
    void getCategoryById_notFound() throws Exception {
        // given
        Long categoryId = 999L;
        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유효한 카테고리를 생성할 수 있다")
    void createCategory() throws Exception {
        // given
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("https://example.com/image.jpg");

        when(categoryService.saveCategory(any(Category.class))).thenReturn(category);

        // when & then
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName").value("한식"))
                .andExpect(jsonPath("$.categoryImage").value("https://example.com/image.jpg"));
    }

    @Test
    @DisplayName("중복된 카테고리 이름으로 생성 시 409 응답을 반환한다")
    void createCategory_duplicateName() throws Exception {
        // given
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("https://example.com/image.jpg");

        when(categoryService.saveCategory(any(Category.class))).thenThrow(new CategoryNameDuplicateException());

        // when & then
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("유효하지 않은 카테고리 이름으로 생성 시 400 응답을 반환한다")
    void createCategory_invalidName() throws Exception {
        // given
        Category category = new Category();
        category.setCategoryName("invalid");
        category.setCategoryImage("https://example.com/image.jpg");

        when(categoryService.saveCategory(any(Category.class))).thenThrow(new CategoryInvalidNameException());

        // when & then
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유효하지 않은 카테고리 이미지로 생성 시 400 응답을 반환한다")
    void createCategory_invalidImage() throws Exception {
        // given
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("invalid-url");

        when(categoryService.saveCategory(any(Category.class))).thenThrow(new CategoryInvalidImageException());

        // when & then
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카테고리를 수정할 수 있다")
    void updateCategory() throws Exception {
        // given
        Long categoryId = 1L;
        Category existingCategory = new Category();
        existingCategory.setCategoryName("기존카테고리");
        existingCategory.setCategoryImage("https://example.com/old.jpg");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("한식");
        updatedCategory.setCategoryImage("https://example.com/image.jpg");

        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryService.saveCategory(any(Category.class))).thenReturn(updatedCategory);

        // when & then
        mockMvc.perform(put("/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("한식"))
                .andExpect(jsonPath("$.categoryImage").value("https://example.com/image.jpg"));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 수정하려고 하면 404 응답을 반환한다")
    void updateCategory_notFound() throws Exception {
        // given
        Long categoryId = 999L;
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("https://example.com/image.jpg");

        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(put("/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리를 삭제할 수 있다")
    void deleteCategory() throws Exception {
        // given
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("https://example.com/image.jpg");

        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.of(category));

        // when & then
        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
        
        verify(categoryService, times(1)).saveCategory(any(Category.class));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 삭제하려고 하면 404 응답을 반환한다")
    void deleteCategory_notFound() throws Exception {
        // given
        Long categoryId = 999L;
        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(delete("/categories/{id}", categoryId))
                .andExpect(status().isNotFound());
    }
} 