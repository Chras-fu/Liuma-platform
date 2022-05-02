package com.autotest.LiuMa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class LiuMaApplication {

	public static void main(String[] args) {

		SpringApplication.run(LiuMaApplication.class, args);
	}

}
