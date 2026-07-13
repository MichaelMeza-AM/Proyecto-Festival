package com.festival.pago_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class PagoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagoServiceApplication.class, args);
	}

	@Bean
	public WebClient webClient() {
		// Al hacerlo así, creas el WebClient directamente sin pedirle el Builder a Spring.
		return WebClient.builder().build();
	}
}
