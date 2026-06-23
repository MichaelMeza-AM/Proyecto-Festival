package com.festival.presentacion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class PresentacionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresentacionServiceApplication.class, args);
    }

    @Bean
    public WebClient webClient(@Value("${api.base-url}") String baseUrl) {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }
}