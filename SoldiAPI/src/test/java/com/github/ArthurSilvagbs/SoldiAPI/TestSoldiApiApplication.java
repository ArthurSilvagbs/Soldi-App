package com.github.ArthurSilvagbs.SoldiAPI;

import org.springframework.boot.SpringApplication;

public class TestSoldiApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SoldiApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
