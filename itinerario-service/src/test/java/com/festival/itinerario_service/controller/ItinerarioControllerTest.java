package com.festival.itinerario_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.festival.itinerario_service.dto.ItinerarioDTO;
import com.festival.itinerario_service.dto.ItinerarioResponseDTO;
import com.festival.itinerario_service.model.Itinerario;
import com.festival.itinerario_service.service.ItinerarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItinerarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItinerarioService itinerarioService;

    // Herramienta para traducir objetos a JSON (como Postman)
    private ObjectMapper objectMapper;

    private ItinerarioResponseDTO responseDTO;
    private ItinerarioDTO requestDTO;
    private Authentication mockAuth;

    @BeforeEach
    void setUp() {
        ItinerarioController controller = new ItinerarioController(itinerarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Inicializamos el traductor y le enseñamos a leer fechas (LocalDateTime)
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // DTO de Entrada: Lo que el usuario nos manda en el Body del POST/PUT
        requestDTO = new ItinerarioDTO();
        requestDTO.setPresentacionId(5L); 

        // DTO de Salida: Lo que el servidor responde
        responseDTO = ItinerarioResponseDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .fechaAgregado(LocalDateTime.now())
                .build();

        // TOKEN FANTASMA: Fingimos que somos el Usuario "1"
        mockAuth = new UsernamePasswordAuthenticationToken(
                "1", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    // --- PRUEBAS GET ---

    @Test
    public void testListarTodos() throws Exception {
        when(itinerarioService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/itinerarios"))
                .andExpect(status().isOk());
                
        verify(itinerarioService, times(1)).listarTodos();
    }

    @Test
    public void testListarMisItinerarios() throws Exception {
        when(itinerarioService.listarPorUsuario(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/itinerarios/me").principal(mockAuth))
                .andExpect(status().isOk());

        verify(itinerarioService, times(1)).listarPorUsuario(1L);
    }

    @Test
    public void testListarPorUsuario() throws Exception {
        // Al pedir la ruta de mi propio usuario (ID 1), el filtro de seguridad me debe dejar pasar
        when(itinerarioService.listarPorUsuario(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/itinerarios/usuario/1").principal(mockAuth))
                .andExpect(status().isOk());

        verify(itinerarioService, times(1)).listarPorUsuario(1L);
    }

    // --- PRUEBA POST ---

    @Test
    public void testCrear() throws Exception {
        Itinerario guardado = new Itinerario();
        guardado.setId(1L);

        // Cuando el controlador intente guardar, le pasamos nuestro objeto de mentira
        when(itinerarioService.guardar(any(Itinerario.class))).thenReturn(guardado);
        when(itinerarioService.obtenerDetalleEnriquecido(guardado)).thenReturn(responseDTO);

        mockMvc.perform(post("/itinerarios")
                        .principal(mockAuth) // Pasamos el Token fantasma
                        .contentType(MediaType.APPLICATION_JSON) // Decimos que es un JSON
                        .content(objectMapper.writeValueAsString(requestDTO))) // Traducimos el DTO a texto
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioId").value(1L));
    }

    // --- PRUEBA PUT ---

    @Test
    public void testActualizar() throws Exception {
        Itinerario existente = new Itinerario();
        existente.setId(1L);
        existente.setUsuarioId(1L); // Le pertenece al usuario "1" del mockAuth

        // Mockeamos la búsqueda para que el controlador verifique que somos los dueños
        when(itinerarioService.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(itinerarioService.actualizar(eq(1L), any(Itinerario.class))).thenReturn(Optional.of(existente));
        when(itinerarioService.obtenerDetalleEnriquecido(any())).thenReturn(responseDTO);

        mockMvc.perform(put("/itinerarios/1")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    // --- PRUEBA DELETE ---

    @Test
    public void testEliminar() throws Exception {
        Itinerario existente = new Itinerario();
        existente.setId(1L);
        existente.setUsuarioId(1L); 
        
        when(itinerarioService.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(itinerarioService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/itinerarios/1").principal(mockAuth))
                .andExpect(status().isNoContent()); 

        verify(itinerarioService, times(1)).eliminar(1L);
    }
}