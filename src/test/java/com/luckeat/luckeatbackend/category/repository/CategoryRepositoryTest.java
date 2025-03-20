package com.luckeat.luckeatbackend.category.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.luckeat.luckeatbackend.category.model.Category;

@Disabled("데이터베이스 설정 문제로 인해 임시로 비활성화")
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리를 저장하고 ID로 조회할 수 있다")
    void saveAndFindById() {
        // given
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("https://example.com/image.jpg");

        // when
        Category savedCategory = categoryRepository.save(category);
        Category foundCategory = categoryRepository.findById(savedCategory.getId()).orElse(null);

        // then
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getCategoryName()).isEqualTo("한식");
        assertThat(foundCategory.getCategoryImage()).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    @DisplayName("삭제되지 않은 카테고리 중 특정 이름의 카테고리가 존재하는지 확인할 수 있다")
    void existsByCategoryNameAndDeletedAtIsNull() {
        // given
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("https://example.com/image.jpg");
        categoryRepository.save(category);

        // when & then
        assertThat(categoryRepository.existsByCategoryNameAndDeletedAtIsNull("한식")).isTrue();
        assertThat(categoryRepository.existsByCategoryNameAndDeletedAtIsNull("양식")).isFalse();
    }

    @Test
    @DisplayName("삭제된 카테고리는 existsByCategoryNameAndDeletedAtIsNull에서 false를 반환한다")
    void existsByCategoryNameAndDeletedAtIsNull_withDeletedCategory() {
        // given
        Category category = new Category();
        category.setCategoryName("한식");
        category.setCategoryImage("https://example.com/image.jpg");
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);

        // when & then
        assertThat(categoryRepository.existsByCategoryNameAndDeletedAtIsNull("한식")).isFalse();
    }

    @Test
    @DisplayName("삭제되지 않은 모든 카테고리를 조회할 수 있다")
    void findByDeletedAtIsNull() {
        // given
        Category category1 = new Category();
        category1.setCategoryName("한식");
        category1.setCategoryImage("https://example.com/image1.jpg");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setCategoryName("양식");
        category2.setCategoryImage("https://example.com/image2.jpg");
        categoryRepository.save(category2);

        Category category3 = new Category();
        category3.setCategoryName("일식");
        category3.setCategoryImage("https://example.com/image3.jpg");
        category3.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category3);

        // when
        List<Category> categories = categoryRepository.findByDeletedAtIsNull();

        // then
        assertThat(categories).hasSize(2);
        assertThat(categories).extracting("categoryName").containsExactlyInAnyOrder("한식", "양식");
        assertThat(categories).extracting("categoryName").doesNotContain("일식");
    }
} 