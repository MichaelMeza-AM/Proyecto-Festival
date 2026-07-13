package com.festival.compra_service.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.festival.compra_service.dto.CompraRequestDTO;
import com.festival.compra_service.model.Compra;
import com.festival.compra_service.service.CompraService;
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
public class CompraControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompraService compraService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Compra compra;
    private CompraRequestDTO requestDto;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        compra = new Compra();
        compra.setId(1L);
        compra.setUsuarioId(2L);
        compra.setEscenarioId(1L);
        compra.setCantidad(2);
        compra.setFechaAsistencia(LocalDateTime.now().plusDays(5));
        compra.setFechaCompra(LocalDateTime.now());

        requestDto = new CompraRequestDTO();
        requestDto.setEscenarioId(1L);
        requestDto.setCantidad(2);
        requestDto.setFechaAsistencia(LocalDateTime.now().plusDays(5));

        auth = new UsernamePasswordAuthenticationToken("2", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        CompraController controller = new CompraController(compraService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testProcesarCompra() throws Exception {
        when(compraService.guardarCompra(eq(2L), any(CompraRequestDTO.class))).thenReturn(compra);

        mockMvc.perform(post("/compras")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testListarMisCompras() throws Exception {
        when(compraService.listarComprasPorUsuario(2L)).thenReturn(List.of(compra));

        mockMvc.perform(get("/compras/me")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testListarTodos() throws Exception {
        when(compraService.listarTodos()).thenReturn(List.of(compra));

        mockMvc.perform(get("/compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(compraService.buscarPorId(1L)).thenReturn(compra);

        mockMvc.perform(get("/compras/1")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testActualizar() throws Exception {
        when(compraService.buscarPorId(1L)).thenReturn(compra);
        when(compraService.actualizar(eq(1L), any(CompraRequestDTO.class))).thenReturn(compra);

        mockMvc.perform(put("/compras/1")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testEliminar() throws Exception {
        when(compraService.buscarPorId(1L)).thenReturn(compra);
        doNothing().when(compraService).eliminar(1L);

        mockMvc.perform(delete("/compras/1")
                        .principal(auth))
                .andExpect(status().isNoContent());
                
        verify(compraService, times(1)).eliminar(1L);
    }
}