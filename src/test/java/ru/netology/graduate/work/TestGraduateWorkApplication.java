package ru.netology.graduate.work;

import org.springframework.boot.SpringApplication;

public class TestGraduateWorkApplication {

	public static void main(String[] args) {
		SpringApplication.from(GraduateWorkApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
