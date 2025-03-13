package com.luckeat.luckeatbackend.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
