package com.example.Library_Management_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class LibraryManagementSystemApplication {

	public static void main(String[] args) {

		var app = new SpringApplication(LibraryManagementSystemApplication.class);
//		SpringApplication.run(LibraryManagementSystemApplication.class, args);
//		app.setDefaultProperties(Collections.singletonMap("spring.profiles.active", "dev"));
		var ctx = app.run(LibraryManagementSystemApplication.class, args);
	}

}
