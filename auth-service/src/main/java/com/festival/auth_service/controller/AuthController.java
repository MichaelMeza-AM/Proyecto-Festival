package com.festival.auth_service.controller;

import com.festival.auth_service.dto.AuthRequestDTO;
import com.festival.auth_service.dto.AuthResponseDTO;
import com.festival.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        String token = authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .token(token)
                        .mensaje("Login exitoso")
                        .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request) {
        String resultado = authService.register(request.getEmail(), request.getPassword());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthResponseDTO.builder()
                        .mensaje(resultado)
                        .build());
    }
}