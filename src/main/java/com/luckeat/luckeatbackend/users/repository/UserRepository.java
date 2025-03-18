package com.luckeat.luckeatbackend.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.luckeat.luckeatbackend.users.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);
	
	// 소프트 딜리트 관련 메소드 추가
	List<User> findByDeletedAtIsNull();
	Optional<User> findByIdAndDeletedAtIsNull(Long id);
	Optional<User> findByEmailAndDeletedAtIsNull(String email);
	Optional<User> findByNicknameAndDeletedAtIsNull(String nickname);
	boolean existsByEmailAndDeletedAtIsNull(String email);
	boolean existsByNicknameAndDeletedAtIsNull(String nickname);
}
