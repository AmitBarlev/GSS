package com.abl.lmd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;

@SpringBootApplication(exclude = {
		MongoReactiveAutoConfiguration.class,
		MongoAutoConfiguration.class,
		MongoReactiveDataAutoConfiguration.class})
public class LmdApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmdApplication.class, args);
	}

}
