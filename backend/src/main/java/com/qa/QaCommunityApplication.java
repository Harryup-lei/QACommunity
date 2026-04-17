package com.qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class QaCommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(QaCommunityApplication.class, args);
    }
}
