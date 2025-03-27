package com.luckeat.luckeatbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LuckeatApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuckeatApplication.class, args);
	}

}
