package com.luckeat.luckeatbackend.review_permission.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.review_permission.model.Permission;
import com.luckeat.luckeatbackend.review_permission.repository.PermissionRepository;
import com.luckeat.luckeatbackend.users.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {
    
    private final PermissionRepository permissionRepository;
    
    public Optional<Permission> getPermission(User user, Product product) {
        return permissionRepository.findByUserAndProduct(user, product);
    }
    
    public boolean canUserReview(User user, Product product) {
        return permissionRepository.findByUserAndProduct(user, product)
                .map(Permission::isCanReview)
                .orElse(false);
    }
    
    @Transactional
    public Permission grantPermission(User user, Product product) {
        Optional<Permission> existingPermission = permissionRepository.findByUserAndProduct(user, product);
        
        if (existingPermission.isPresent()) {
            Permission permission = existingPermission.get();
            permission.setCanReview(true);
            return permissionRepository.save(permission);
        } else {
            Permission permission = Permission.builder()
                    .user(user)
                    .product(product)
                    .canReview(true)
                    .build();
            return permissionRepository.save(permission);
        }
    }
    
    @Transactional
    public Permission revokePermission(User user, Product product) {
        Optional<Permission> existingPermission = permissionRepository.findByUserAndProduct(user, product);
        
        if (existingPermission.isPresent()) {
            Permission permission = existingPermission.get();
            permission.setCanReview(false);
            return permissionRepository.save(permission);
        } else {
            Permission permission = Permission.builder()
                    .user(user)
                    .product(product)
                    .canReview(false)
                    .build();
            return permissionRepository.save(permission);
        }
    }
}
