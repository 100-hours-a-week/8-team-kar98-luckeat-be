package com.luckeat.luckeatbackend.users.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luckeat.luckeatbackend.users.model.User;
import com.luckeat.luckeatbackend.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Optional<User> getUserById(Long userId) {
		return userRepository.findById(userId);
	}

	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<User> getUserByNickname(String nickname) {
		return userRepository.findByNickname(nickname);
	}

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }


	@Transactional
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Transactional
	public void deleteUser(Long userId) {
		userRepository.deleteById(userId);
	}

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public boolean existsByNickname(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

	// 사용자 인증 (로그인)
	public Optional<User> authenticateUser(String email, String password) {
		// TODO : 로그인 기능 구현
		return null;
	}
}
