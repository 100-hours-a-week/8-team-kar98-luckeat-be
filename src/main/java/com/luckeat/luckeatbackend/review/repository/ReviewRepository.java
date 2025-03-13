package com.luckeat.luckeatbackend.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.product.model.Product;
import com.luckeat.luckeatbackend.review.model.Review;
import com.luckeat.luckeatbackend.users.model.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);
    List<Review> findByProduct(Product product);
}
