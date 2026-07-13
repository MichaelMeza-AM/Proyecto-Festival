package com.festival.auth_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.festival.auth_service.exception.BadRequestException;
import com.festival.auth_service.exception.UnauthorizedException;
import com.festival.auth_service.model.User;
import com.festival.auth_service.repository.UserRepository;

import reactor.core.publisher.Mono;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HashService hashService;

    @Mock
    private WebClient webClient;

    // --- Mocks encadenados para el WebClient ---
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AuthService authService;

    private User usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = new User(1L, "fan@gmail.com", "hash_sha1_valido", "ROLE_USER");
    }

    @Test
    void testLogin_Exitoso() {
        when(userRepository.findByEmail("fan@gmail.com")).thenReturn(Optional.of(usuarioValido));
        when(hashService.sha1("password123")).thenReturn("hash_sha1_valido");
        when(jwtService.generateToken(1L, "fan@gmail.com", "ROLE_USER")).thenReturn("token_jwt_falso_exitoso");

        String tokenResultado = authService.login("fan@gmail.com", "password123");

        assertNotNull(tokenResultado);
        assertEquals("token_jwt_falso_exitoso", tokenResultado);
        verify(userRepository, times(1)).findByEmail("fan@gmail.com");
    }

    @Test
    void testLogin_UsuarioNoExiste_LanzaUnauthorizedException() {
        when(userRepository.findByEmail("no_existe@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login("no_existe@gmail.com", "password123"));
        verify(jwtService, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void testLogin_ContrasenaInvalida_LanzaUnauthorizedException() {
        when(userRepository.findByEmail("fan@gmail.com")).thenReturn(Optional.of(usuarioValido));
        when(hashService.sha1("clave_erronea")).thenReturn("hash_incorrecto");

        assertThrows(UnauthorizedException.class, () -> authService.login("fan@gmail.com", "clave_erronea"));
        verify(jwtService, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void testRegister_Exitoso() {
        // 1. Preparamos los mocks básicos
        when(userRepository.existsByEmail("nuevo@gmail.com")).thenReturn(false);
        when(hashService.sha1("password123")).thenReturn("hashed_pw");
        when(userRepository.save(any(User.class))).thenReturn(usuarioValido);

        // 2. Simulamos la cadena del WebClient para que retorne ÉXITO
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Usuario creado"));

        // 3. Ejecutamos el método
        String resultado = authService.register("nuevo@gmail.com", "password123");

        // 4. Verificaciones (Assserts)
        assertEquals("Usuario y Perfil creados exitosamente!", resultado);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, never()).delete(any(User.class)); // Aseguramos que NO hizo rollback
    }

    @Test
    void testRegister_FalloWebClient_HaceRollbackManual() {
        // 1. Preparamos los mocks básicos
        when(userRepository.existsByEmail("nuevo@gmail.com")).thenReturn(false);
        when(hashService.sha1("password123")).thenReturn("hashed_pw");
        when(userRepository.save(any(User.class))).thenReturn(usuarioValido);

        // 2. Simulamos la cadena del WebClient para que retorne un ERROR (Simula servicio caído)
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("Conexión rechazada")));

        // 3. Ejecutamos el método y esperamos la excepción
        assertThrows(BadRequestException.class, () -> authService.register("nuevo@gmail.com", "password123"));

        // 4. Verificaciones críticas de Rollback
        verify(userRepository, times(1)).save(any(User.class)); // Verificamos que intentó guardarlo
        verify(userRepository, times(1)).delete(any(User.class)); // VERIFICAMOS QUE LO BORRÓ (Rollback)
    }

    @Test
    void testRegister_UsuarioYaExiste_LanzaBadRequestException() {
        when(userRepository.existsByEmail("fan@gmail.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register("fan@gmail.com", "password123"));
        verify(userRepository, never()).save(any(User.class));
    }

}