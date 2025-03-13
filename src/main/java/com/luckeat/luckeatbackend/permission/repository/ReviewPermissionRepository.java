package com.luckeat.luckeatbackend.permission.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.permission.model.ReviewPermission;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.users.model.User;

@Repository
public interface ReviewPermissionRepository extends JpaRepository<ReviewPermission, Long> {
	Optional<ReviewPermission> findByUserAndProduct(User user, Product product);
}
