package com.festival.artista_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.festival.artista_service.model.Artista;
import com.festival.artista_service.dto.ArtistaDTO;
import com.festival.artista_service.service.ArtistaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ArtistaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ArtistaService artistaService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Artista artista;
    private ArtistaDTO artistaDto;

    @BeforeEach
    void setUp() {
        // Modelo simulado que devolverá el servicio
        artista = new Artista(
            1L,
            "Los Prisioneros",
            "Banda icónica de rock chileno.",
            "Rock"
        );

        // DTO enviado en el cuerpo de las peticiones de creación o actualización
        artistaDto = new ArtistaDTO(
            null,
            "Los Prisioneros",
            "Banda icónica de rock chileno.",
            "Rock"
        );

        ArtistaController controller = new ArtistaController(artistaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testListarArtistas() throws Exception {
        when(artistaService.listar()).thenReturn(List.of(artista));

        mockMvc.perform(get("/artistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Los Prisioneros"))
                .andExpect(jsonPath("$[0].biografia").value("Banda icónica de rock chileno."))
                .andExpect(jsonPath("$[0].generoMusical").value("Rock"));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(artistaService.buscarPorId(1L)).thenReturn(Optional.of(artista));

        mockMvc.perform(get("/artistas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Los Prisioneros"))
                .andExpect(jsonPath("$.biografia").value("Banda icónica de rock chileno."))
                .andExpect(jsonPath("$.generoMusical").value("Rock"));
    }

    @Test
    public void testCrearArtista() throws Exception {
        when(artistaService.guardar(any(Artista.class))).thenReturn(artista);

        mockMvc.perform(post("/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artistaDto)))
                .andExpect(status().isCreated()) // Tu controlador responde con 201 Created
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Los Prisioneros"))
                .andExpect(jsonPath("$.generoMusical").value("Rock"));
    }

    @Test
    public void testActualizarArtista() throws Exception {
        when(artistaService.actualizar(eq(1L), any(Artista.class))).thenReturn(Optional.of(artista));

        mockMvc.perform(put("/artistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artistaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Los Prisioneros"))
                .andExpect(jsonPath("$.generoMusical").value("Rock"));
    }

    @Test
    public void testEliminarArtista() throws Exception {
        // Tu controlador primero verifica si existe el ID antes de borrar
        when(artistaService.existePorId(1L)).thenReturn(true);
        doNothing().when(artistaService).eliminar(1L);

        mockMvc.perform(delete("/artistas/1"))
                .andExpect(status().isNoContent()); // Tu controlador devuelve noContent() (204)
        
        verify(artistaService, times(1)).eliminar(1L);
    }
}