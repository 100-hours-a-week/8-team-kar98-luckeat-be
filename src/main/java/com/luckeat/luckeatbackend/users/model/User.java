package com.luckeat.luckeatbackend.users.model;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role; // 회원 역할 (ENUM)

	@Column(nullable = false, length = 255)
	private String email; // 회원 이메일

	@Column(nullable = false, length = 255)
	private String password; // 회원 비밀번호 (SHA-256 암호화)

	@Column(nullable = false, unique = true, length = 50)
	private String nickname; // 회원 닉네임 (UNIQUE)

	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private Integer totalSavedMoney; // 총 아낀 금액

	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalProductCount; // 총 상품 갯수

	public enum Role {
		BUYER, SELLER, ADMIN
	}
}
