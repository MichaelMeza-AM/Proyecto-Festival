package com.festival.pago_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.festival.pago_service.dto.PagoRequestDTO;
import com.festival.pago_service.model.Pago;
import com.festival.pago_service.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PagoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PagoService pagoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Pago pago;
    private PagoRequestDTO requestDto;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        pago = new Pago();
        pago.setId(1L);
        pago.setUsuarioId(2L);
        pago.setIdCompra(1L);
        pago.setMontoSubtotal(1000);
        pago.setPorcentajeDescuento(10);
        pago.setMontoDescuento(100);
        pago.setIva(171);
        pago.setMontoTotal(1071);
        pago.setMedioPago("Tarjeta");
        pago.setFechaPago(LocalDateTime.now());

        requestDto = new PagoRequestDTO();
        requestDto.setIdCompra(1L);
        requestDto.setMedioPago("Tarjeta");
        requestDto.setPorcentajeDescuento(10);

        auth = new UsernamePasswordAuthenticationToken("2", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        PagoController controller = new PagoController(pagoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testListarTodos() throws Exception {
        when(pagoService.listarTodos()).thenReturn(List.of(pago));

        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].montoTotal").value(1071));
    }

    @Test
    public void testBuscarPorId() throws Exception {
        when(pagoService.buscarPorId(1L)).thenReturn(pago);

        mockMvc.perform(get("/pagos/1")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.medioPago").value("Tarjeta"));
    }

    @Test
    public void testProcesarPago() throws Exception {
        when(pagoService.procesarPago(any(PagoRequestDTO.class), anyString(), eq(2L))).thenReturn(pago);

        mockMvc.perform(post("/pagos")
                        .principal(auth)
                        .header("Authorization", "Bearer token-falso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testActualizar() throws Exception {
        when(pagoService.buscarPorId(1L)).thenReturn(pago);
        when(pagoService.actualizar(eq(1L), any(PagoRequestDTO.class))).thenReturn(pago);

        mockMvc.perform(put("/pagos/1")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testEliminar() throws Exception {
        when(pagoService.buscarPorId(1L)).thenReturn(pago);
        doNothing().when(pagoService).eliminar(1L);
        
        mockMvc.perform(delete("/pagos/1")
                        .principal(auth))
                .andExpect(status().isNoContent());
                
        verify(pagoService, times(1)).eliminar(1L);
    }
}