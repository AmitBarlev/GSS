package com.abl.gss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;

@SpringBootApplication(exclude = {
		MongoReactiveAutoConfiguration.class,
		MongoAutoConfiguration.class,
		MongoReactiveDataAutoConfiguration.class})
public class GssApplication {

	public static void main(String[] args) {
		SpringApplication.run(GssApplication.class, args);
	}

}
