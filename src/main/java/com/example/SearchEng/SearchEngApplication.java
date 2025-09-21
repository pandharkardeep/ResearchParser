package com.example.SearchEng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class SearchEngApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchEngApplication.class, args);
	}

}
