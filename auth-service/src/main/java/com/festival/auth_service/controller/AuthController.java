package com.festival.auth_service.controller;

import com.festival.auth_service.dto.AuthRequestDTO;
import com.festival.auth_service.dto.AuthResponseDTO;
import com.festival.auth_service.service.AuthService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        logger.info("Petición POST /auth/login - Intento de inicio de sesión para: {}", request.getEmail());
        String token = authService.login(request.getEmail(), request.getPassword());

        logger.info("Login completado con éxito para el usuario: {}", request.getEmail());
        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .token(token)
                        .mensaje("Login exitoso")
                        .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request) {
        logger.info("Petición POST /auth/register - Solicitud de registro para el email: {}", request.getEmail());
        String resultado = authService.register(request.getEmail(), request.getPassword());
        
        logger.info("Registro completado con éxito para el usuario: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthResponseDTO.builder()
                        .mensaje(resultado)
                        .build());
    }
}