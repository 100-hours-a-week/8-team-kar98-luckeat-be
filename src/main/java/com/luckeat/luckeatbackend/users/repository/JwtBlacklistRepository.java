package com.luckeat.luckeatbackend.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.luckeat.luckeatbackend.users.model.JwtBlacklist;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {
    boolean existsByToken(String token);
} 