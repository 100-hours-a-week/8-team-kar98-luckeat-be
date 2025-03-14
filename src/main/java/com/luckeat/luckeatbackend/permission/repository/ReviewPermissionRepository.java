package com.luckeat.luckeatbackend.permission.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.permission.model.ReviewPermission;

@Repository
public interface ReviewPermissionRepository extends JpaRepository<ReviewPermission, Long> {

	Optional<ReviewPermission> findByUserIdAndStoreId(Long userId, Long storeId);
}
