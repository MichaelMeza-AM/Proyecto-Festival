package com.festival.usuario_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.festival.usuario_service.model.Usuario;
import com.festival.usuario_service.dto.UsuarioDTO;
import com.festival.usuario_service.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    private ObjectMapper objectMapper;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private Authentication mockAuthAdmin;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        usuario = new Usuario(1L, "Admin Fest", "admin@fest.cl", "11111111-1", LocalDate.of(1990, 1, 1));
        usuarioDTO = UsuarioDTO.fromModel(usuario);

        // Simulamos un Token de un usuario con ID "1" y rol "ADMIN"
        mockAuthAdmin = new UsernamePasswordAuthenticationToken(
                "1", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        UsuarioController controller = new UsuarioController(usuarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testExisteUsuario() throws Exception {
        when(usuarioService.existePorId(1L)).thenReturn(true);
        mockMvc.perform(get("/usuarios/exists/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testListarUsuarios() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(usuario));
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Admin Fest"));
    }

    @Test
    public void testObtenerUsuarioPorId() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        
        mockMvc.perform(get("/usuarios/1").principal(mockAuthAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@fest.cl"));
    }

    @Test
    public void testObtenerMiPerfil() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        
        mockMvc.perform(get("/usuarios/me").principal(mockAuthAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Admin Fest"));
    }

    @Test
    public void testCrearPerfil() throws Exception {
        when(usuarioService.guardar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testActualizarUsuario() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(usuarioService.actualizarUsuario(eq(1L), anyString(), anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(put("/usuarios/1")
                        .principal(mockAuthAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Admin Fest"));
    }

    @Test
    public void testEliminarUsuario() throws Exception {
        when(usuarioService.eliminarUsuario(1L)).thenReturn(true);

        mockMvc.perform(delete("/usuarios/1").principal(mockAuthAdmin))
                .andExpect(status().isNoContent());
        
        verify(usuarioService, times(1)).eliminarUsuario(1L);
    }
}