package com.luckeat.luckeatbackend.users.service;

import java.time.LocalDateTime;
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

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<User> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Transactional
	public User createUser(User user) {
		user.setCreatedAt(LocalDateTime.now());
		return userRepository.save(user);
	}

	@Transactional
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
}
