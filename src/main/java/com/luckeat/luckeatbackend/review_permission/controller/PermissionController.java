package com.luckeat.luckeatbackend.review_permission.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.product.service.ProductService;
import com.luckeat.luckeatbackend.review_permission.model.Permission;
import com.luckeat.luckeatbackend.review_permission.service.PermissionService;
import com.luckeat.luckeatbackend.users.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    
    private final PermissionService permissionService;
    private final UserService userService;
    private final ProductService productService;
    
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkPermission(@RequestParam Long userId, @RequestParam Long productId) {
        return userService.getUserById(userId)
                .flatMap(user -> productService.getProductById(productId)
                        .map(product -> ResponseEntity.ok(permissionService.canUserReview(user, product))))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/grant")
    public ResponseEntity<Permission> grantPermission(@RequestParam Long userId, @RequestParam Long productId) {
        return userService.getUserById(userId)
                .flatMap(user -> productService.getProductById(productId)
                        .map(product -> ResponseEntity.status(HttpStatus.CREATED)
                                .body(permissionService.grantPermission(user, product))))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/revoke")
    public ResponseEntity<Permission> revokePermission(@RequestParam Long userId, @RequestParam Long productId) {
        return userService.getUserById(userId)
                .flatMap(user -> productService.getProductById(productId)
                        .map(product -> ResponseEntity.ok(permissionService.revokePermission(user, product))))
                .orElse(ResponseEntity.notFound().build());
    }
}
