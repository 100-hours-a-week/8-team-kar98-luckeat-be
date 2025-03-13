package com.luckeat.luckeatbackend.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luckeat.luckeatbackend.product.service.ProductService;
import com.luckeat.luckeatbackend.review.model.Review;
import com.luckeat.luckeatbackend.review.service.ReviewService;
import com.luckeat.luckeatbackend.review_permission.service.PermissionService;
import com.luckeat.luckeatbackend.users.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    private final ProductService productService;
    private final UserService userService;
    private final PermissionService permissionService;
    
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(reviewService.getReviewsByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        return productService.getProductById(productId)
                .map(product -> ResponseEntity.ok(reviewService.getReviewsByProduct(product)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        return userService.getUserById(review.getUser().getId())
                .flatMap(user -> productService.getProductById(review.getProduct().getId())
                        .map(product -> {
                            if (permissionService.canUserReview(user, product)) {
                                review.setUser(user);
                                review.setProduct(product);
                                return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(review));
                            } else {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission to review this product");
                            }
                        }))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(review -> {
                    reviewService.deleteReview(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
