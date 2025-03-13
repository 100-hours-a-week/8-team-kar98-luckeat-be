package com.luckeat.luckeatbackend.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.category.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
