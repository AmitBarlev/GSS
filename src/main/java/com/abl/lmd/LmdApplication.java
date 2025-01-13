package com.abl.lmd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class LmdApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmdApplication.class, args);
	}

}
