package com.example.authenticationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.authenticationserver.repository")
public class AuthenticationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServerApplication.class, args);
    }

}
