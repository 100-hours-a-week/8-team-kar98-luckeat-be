package com.luckeat.luckeatbackend.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.category.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByDeletedAtIsNull();
	boolean existsByCategoryNameAndDeletedAtIsNull(String categoryName);
	Optional<Category> findByCategoryNameAndDeletedAtIsNull(String categoryName);
	Optional<Category> findByIdAndDeletedAtIsNull(Long id);
	List<Category> findByDeletedAtIsNullOrderByCategoryNameAsc();
	List<Category> findByCategoryNameContainingAndDeletedAtIsNull(String keyword);
	List<Category> findByIdInAndDeletedAtIsNull(List<Long> ids);
}
