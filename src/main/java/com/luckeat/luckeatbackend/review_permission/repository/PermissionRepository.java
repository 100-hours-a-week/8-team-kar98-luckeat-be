package com.luckeat.luckeatbackend.review_permission.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.review_permission.model.Permission;
import com.luckeat.luckeatbackend.users.model.User;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByUserAndProduct(User user, Product product);
}
