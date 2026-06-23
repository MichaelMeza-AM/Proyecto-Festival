package com.festival.auth_service.service;

import com.festival.auth_service.exception.BadRequestException;
import com.festival.auth_service.model.User;
import com.festival.auth_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final HashService hashService;
    private final WebClient webClient;

    public UserService(UserRepository userRepository, JwtService jwtService, HashService hashService, WebClient webClient) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.hashService = hashService;
        this.webClient = webClient;
    }

    public String login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return null;
        }
        
        User user = userOpt.get();
        String hashedInput = hashService.sha1(password);
        
        if (!hashedInput.equals(user.getPassword())) {
            return null;
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

        Map<String, String> nuevoPerfil = new HashMap<>();
        nuevoPerfil.put("id", String.valueOf(user.getId())); 
        nuevoPerfil.put("email", email);
        nuevoPerfil.put("nombre", "Asistente Nuevo"); 
        
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