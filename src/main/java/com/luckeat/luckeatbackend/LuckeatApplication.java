package com.luckeat.luckeatbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class LuckeatApplication {

	public static void main(String[] args) {
		// 로그 디렉토리 생성
		createLogDirectories();
		
		SpringApplication.run(LuckeatApplication.class, args);
	}
	
	/**
	 * 로그 디렉토리 생성
	 * 애플리케이션 시작 시 필요한 로그 디렉토리가 없으면 자동으로 생성합니다.
	 */
	private static void createLogDirectories() {
		try {
			// 기본 로그 디렉토리
			Path logPath = Paths.get("logs");
			if (!Files.exists(logPath)) {
				Files.createDirectories(logPath);
				System.out.println("로그 디렉토리가 생성되었습니다: " + logPath.toAbsolutePath());
			}
			
			// 보관용 로그 디렉토리
			Path archivedPath = Paths.get("logs/archived");
			if (!Files.exists(archivedPath)) {
				Files.createDirectories(archivedPath);
				System.out.println("보관용 로그 디렉토리가 생성되었습니다: " + archivedPath.toAbsolutePath());
			}
		} catch (Exception e) {
			System.err.println("로그 디렉토리 생성 중 오류 발생: " + e.getMessage());
		}
	}
}
