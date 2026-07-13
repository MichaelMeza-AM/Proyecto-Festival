package com.festival.auth_service.service;

import com.festival.auth_service.dto.UsuarioRegistroDTO;
import com.festival.auth_service.exception.BadRequestException;
import com.festival.auth_service.exception.UnauthorizedException;
import com.festival.auth_service.model.User;
import com.festival.auth_service.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
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
        
        logger.debug("Buscando usuario en la base de datos con email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Fallo de inicio de sesión: El email '{}' no está registrado.", email);
                    return new UnauthorizedException("Credenciales incorrectas");
                });
        
        String hashedInput = hashService.sha1(password);
        
        if (!hashedInput.equals(user.getPassword())) {
            logger.warn("Fallo de inicio de sesión: Contraseña inválida para el usuario: {}", email);
            throw new UnauthorizedException("Credenciales incorrectas");
        }
        
        logger.info("Credenciales validadas correctamente. Generando JWT para el ID: {}", user.getId());
        return jwtService.generateToken(user.getId(), email, user.getRole());
    }

    public String register(String email, String password) {
        
        logger.debug("Verificando disponibilidad del email para registro: {}", email);
        if (userRepository.existsByEmail(email)) {
            logger.warn("Fallo de registro: Intento de duplicación para el email '{}'.", email);
            throw new BadRequestException("El usuario ya existe."); 
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashService.sha1(password));
        user.setRole("ROLE_USER");
       
        userRepository.save(user);
        logger.info("Credenciales base guardadas en 'users' con ID asignado: {}", user.getId());

        UsuarioRegistroDTO nuevoPerfil = UsuarioRegistroDTO.builder()
                .id(user.getId())
                .email(email)
                .nombre("Asistente Nuevo") 
                .build();
        
        try {
            logger.info("Sincronizando datos: Enviando comando WebClient hacia usuario-service para crear perfil del ID: {}", user.getId());

            webClient.post()
                    .uri("/usuarios")
                    .bodyValue(nuevoPerfil) 
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            logger.info("Sincronización exitosa. Perfil creado en usuario-service.");
            return "Usuario y Perfil creados exitosamente!";
        } catch (Exception e) {
            logger.error("Error crítico de consistencia: No se pudo crear el perfil para el ID={}. Revirtiendo registro en Auth. Causa: {}", user.getId(), e.getMessage());
            userRepository.delete(user);
            throw new BadRequestException("Credenciales creadas, pero el servicio de perfiles no responde.");
        }
    }    
}