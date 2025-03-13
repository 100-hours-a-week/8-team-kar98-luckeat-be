package com.luckeat.luckeatbackend.permission.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.permission.model.ReviewPermission;
import com.luckeat.luckeatbackend.permission.repository.ReviewPermissionRepository;
import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.users.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewPermissionService {

	private final ReviewPermissionRepository permissionRepository;

	public Optional<ReviewPermission> getPermission(User user, Product product) {
		return permissionRepository.findByUserAndProduct(user, product);
	}

	public boolean canUserReview(User user, Product product) {
		return permissionRepository.findByUserAndProduct(user, product).map(ReviewPermission::isCanReview)
				.orElse(false);
	}

	@Transactional
	public ReviewPermission grantPermission(User user, Product product) {
		Optional<ReviewPermission> existingPermission = permissionRepository.findByUserAndProduct(user, product);

		if (existingPermission.isPresent()) {
			ReviewPermission permission = existingPermission.get();
			permission.setCanReview(true);
			return permissionRepository.save(permission);
		} else {
			ReviewPermission permission = ReviewPermission.builder().user(user).product(product).canReview(true)
					.build();
			return permissionRepository.save(permission);
		}
	}

	@Transactional
	public ReviewPermission revokePermission(User user, Product product) {
		Optional<ReviewPermission> existingPermission = permissionRepository.findByUserAndProduct(user, product);

		if (existingPermission.isPresent()) {
			ReviewPermission permission = existingPermission.get();
			permission.setCanReview(false);
			return permissionRepository.save(permission);
		} else {
			ReviewPermission permission = ReviewPermission.builder().user(user).product(product).canReview(false)
					.build();
			return permissionRepository.save(permission);
		}
	}
}
