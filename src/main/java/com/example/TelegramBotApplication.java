package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * This is a standard spring-boot main class.
 */
@SpringBootApplication
public class TelegramBotApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

}
