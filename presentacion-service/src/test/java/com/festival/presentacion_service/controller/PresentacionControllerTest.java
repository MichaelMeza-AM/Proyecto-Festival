package com.festival.presentacion_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.festival.presentacion_service.model.Presentacion;
import com.festival.presentacion_service.dto.PresentacionRequestDTO;
import com.festival.presentacion_service.dto.PresentacionResponseDTO;
import com.festival.presentacion_service.service.PresentacionService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PresentacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PresentacionService presentacionService;

    private ObjectMapper objectMapper;
    private Presentacion presentacion;
    private PresentacionRequestDTO requestDto;
    private PresentacionResponseDTO responseDto;
    private LocalDateTime fechaTest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Necesario para parsear LocalDateTime

        fechaTest = LocalDateTime.of(2026, 11, 20, 17, 0);

        presentacion = new Presentacion(1L, 4L, 1L, fechaTest, 90);
        
        requestDto = new PresentacionRequestDTO(4L, 1L, fechaTest, 90);
        
        responseDto = PresentacionResponseDTO.builder()
                .id(1L)
                .artistaId(4L)
                .escenarioId(1L)
                .nombreArtista("Bad Bunny")
                .nombreEscenario("Main Stage")
                .fechaHora(fechaTest)
                .duracionMinutos(90)
                .build();

        PresentacionController controller = new PresentacionController(presentacionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testListar() throws Exception {
        when(presentacionService.listar()).thenReturn(List.of(presentacion));
        when(presentacionService.obtenerDetalle(any(Presentacion.class))).thenReturn(responseDto);

        mockMvc.perform(get("/presentaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombreArtista").value("Bad Bunny"));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(presentacionService.buscarPorId(1L)).thenReturn(presentacion);
        when(presentacionService.obtenerDetalle(any(Presentacion.class))).thenReturn(responseDto);

        mockMvc.perform(get("/presentaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreEscenario").value("Main Stage"));
    }

    @Test
    public void testCrear() throws Exception {
        when(presentacionService.guardar(any(Presentacion.class))).thenReturn(presentacion);
        when(presentacionService.obtenerDetalle(any(Presentacion.class))).thenReturn(responseDto);

        mockMvc.perform(post("/presentaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombreArtista").value("Bad Bunny"));
    }

    @Test
    public void testActualizar() throws Exception {
        when(presentacionService.actualizar(eq(1L), any(Presentacion.class))).thenReturn(presentacion);
        when(presentacionService.obtenerDetalle(any(Presentacion.class))).thenReturn(responseDto);

        mockMvc.perform(put("/presentaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testEliminar() throws Exception {
        doNothing().when(presentacionService).eliminar(1L);

        mockMvc.perform(delete("/presentaciones/1"))
                .andExpect(status().isNoContent());
        
        verify(presentacionService, times(1)).eliminar(1L);
    }

    @Test
    public void testBuscarPorDia() throws Exception {
        // Simulamos que el servicio devuelve la lista de presentaciones para esa fecha
        when(presentacionService.buscarPorDia(any(java.time.LocalDate.class))).thenReturn(List.of(presentacion));
        when(presentacionService.obtenerDetalle(any(Presentacion.class))).thenReturn(responseDto);

        // Realizamos la petición GET enviando el parámetro "fecha" en el formato requerido (dd-MM-yyyy)
        mockMvc.perform(get("/presentaciones/dia")
                        .param("fecha", "20-11-2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombreArtista").value("Bad Bunny"));
    }
}