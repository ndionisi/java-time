package com.github.ndionisi.datetime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
public class JavaTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaTimeApplication.class, args);
    }
}
