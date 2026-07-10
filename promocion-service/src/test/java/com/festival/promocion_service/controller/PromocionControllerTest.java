package com.festival.promocion_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.festival.promocion_service.dto.PromocionRequestDTO;
import com.festival.promocion_service.dto.PromocionResponseDTO;
import com.festival.promocion_service.model.Promocion;
import com.festival.promocion_service.service.PromocionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PromocionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PromocionService promocionService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Promocion promocion;
    private PromocionRequestDTO requestDto;
    private PromocionResponseDTO responseDto;

    @BeforeEach
    void setUp() {
        // REGISTRO CRUCIAL: Habilita el soporte para procesar LocalDateTime de Java 8
        objectMapper.registerModule(new JavaTimeModule());

        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 12, 31, 23, 59, 59);

        promocion = new Promocion(1L, "FESTIVAL2026", 15, inicio, fin, true);

        requestDto = new PromocionRequestDTO();
        requestDto.setCodigo("FESTIVAL2026");
        requestDto.setPorcentajeDescuento(15);
        requestDto.setFechaInicio(inicio);
        requestDto.setFechaFin(fin);

        responseDto = new PromocionResponseDTO();
        responseDto.setId(1L);
        responseDto.setCodigo("FESTIVAL2026");
        responseDto.setPorcentajeDescuento(15);
        responseDto.setEsValido(true);
        responseDto.setMensaje("Aplicado");

        PromocionController controller = new PromocionController(promocionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testCrearPromocion() throws Exception {
        when(promocionService.guardar(any(Promocion.class))).thenReturn(promocion);

        mockMvc.perform(post("/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("FESTIVAL2026"));
    }

    @Test
    public void testListarTodas() throws Exception {
        when(promocionService.listarTodas()).thenReturn(List.of(promocion));

        mockMvc.perform(get("/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].porcentajeDescuento").value(15));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(promocionService.buscarPorId(1L)).thenReturn(promocion);

        mockMvc.perform(get("/promociones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("FESTIVAL2026"));
    }

    @Test
    public void testActualizarPromocion() throws Exception {
        when(promocionService.actualizar(eq(1L), any(Promocion.class))).thenReturn(promocion);

        mockMvc.perform(put("/promociones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testEliminarPromocion() throws Exception {
        doNothing().when(promocionService).eliminar(1L);

        mockMvc.perform(delete("/promociones/1"))
                .andExpect(status().isNoContent());

        verify(promocionService, times(1)).eliminar(1L);
    }

    @Test
    public void testValidarPromocionEndpoint() throws Exception {
        when(promocionService.validarPromocion("FESTIVAL2026")).thenReturn(responseDto);

        mockMvc.perform(get("/promociones/validar/FESTIVAL2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esValido").value(true))
                .andExpect(jsonPath("$.mensaje").value("Aplicado"));
    }
}