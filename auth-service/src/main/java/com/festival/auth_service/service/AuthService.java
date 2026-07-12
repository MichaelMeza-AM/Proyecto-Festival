package com.festival.auth_service.service;

import com.festival.auth_service.dto.UsuarioRegistroDTO;
import com.festival.auth_service.exception.BadRequestException;
import com.festival.auth_service.exception.UnauthorizedException;
import com.festival.auth_service.model.User;
import com.festival.auth_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final HashService hashService;
    private final WebClient webClient;

    public AuthService(UserRepository userRepository, JwtService jwtService, HashService hashService, WebClient webClient) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.hashService = hashService;
        this.webClient = webClient;
    }

    public String login(String email, String password) {
        // Buscamos al usuario. Si no existe, lanzamos la excepción automáticamente
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));
        
        String hashedInput = hashService.sha1(password);
        
        // Verificamos la contraseña
        if (!hashedInput.equals(user.getPassword())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }
        
        return jwtService.generateToken(user.getId(), email, user.getRole());
    }

    public String register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("El usuario ya existe."); 
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashService.sha1(password));
        user.setRole("ROLE_USER");
        userRepository.save(user);

        UsuarioRegistroDTO nuevoPerfil = UsuarioRegistroDTO.builder()
                .id(user.getId())
                .email(email)
                .nombre("Asistente Nuevo") 
                .build();
        
        try {
            webClient.post()
                    .uri("/usuarios")
                    .bodyValue(nuevoPerfil) 
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return "Usuario y Perfil creados exitosamente!";
        } catch (Exception e) {
            throw new BadRequestException("Credenciales creadas, pero el servicio de perfiles no responde.");
        }
    }    
}