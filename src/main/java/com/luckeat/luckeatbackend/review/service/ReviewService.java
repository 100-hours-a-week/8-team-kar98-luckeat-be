package com.luckeat.luckeatbackend.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.review.model.Review;
import com.luckeat.luckeatbackend.review.repository.ReviewRepository;
import com.luckeat.luckeatbackend.users.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }
    
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }
    
    public List<Review> getReviewsByProduct(Product product) {
        return reviewRepository.findByProduct(product);
    }
    
    @Transactional
    public Review createReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }
    
    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
