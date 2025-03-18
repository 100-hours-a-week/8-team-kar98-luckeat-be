package com.luckeat.luckeatbackend.users.model;

import com.luckeat.luckeatbackend.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_users_nickname_active",
            columnNames = {"nickname", "deleted_at"}
        )
    }
)
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

	@Column(nullable = false, length = 50)
	private String nickname; // 회원 닉네임

	public enum Role {
		BUYER, SELLER, ADMIN
	}
}
