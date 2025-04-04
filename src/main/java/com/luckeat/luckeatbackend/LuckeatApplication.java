package com.luckeat.luckeatbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LuckeatApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuckeatApplication.class, args);
	}

}
