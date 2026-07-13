package com.festival.auth_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festival.auth_service.dto.AuthRequestDTO;
import com.festival.auth_service.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private AuthRequestDTO requestDto;

    @BeforeEach
    void setUp() {
        // Inicializamos el controlador pasándole el servicio simulado
        AuthController controller = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Preparamos el DTO de prueba que enviaremos en el body
        requestDto = new AuthRequestDTO();
        requestDto.setEmail("fan@gmail.com");
        requestDto.setPassword("password123");
    }

    @Test
    public void testLogin() throws Exception {
        // 1. Simulamos que el servicio devuelve un token válido
        when(authService.login("fan@gmail.com", "password123")).thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        // 2. Ejecutamos la petición POST y verificamos código 200 y el JSON de respuesta
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"));
        
        verify(authService, times(1)).login("fan@gmail.com", "password123");
    }

    @Test
    public void testRegister() throws Exception {
        // 1. Simulamos el mensaje de éxito del registro
        when(authService.register("fan@gmail.com", "password123")).thenReturn("Usuario y Perfil creados exitosamente!");

        // 2. Ejecutamos la petición POST y verificamos código 201 (CREATED)
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Usuario y Perfil creados exitosamente!"));
                
        verify(authService, times(1)).register("fan@gmail.com", "password123");
    }
}