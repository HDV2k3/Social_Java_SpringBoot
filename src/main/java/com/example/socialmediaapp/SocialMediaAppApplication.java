package com.example.socialmediaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.socialmediaapp")
public class SocialMediaAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialMediaAppApplication.class, args);
    }

}
