package com.festival.ticket_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.festival.ticket_service.dto.TicketRequestDTO;
import com.festival.ticket_service.model.Ticket;
import com.festival.ticket_service.service.TicketService;
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
public class TicketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketService ticketService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Ticket ticket;
    private TicketRequestDTO requestDto;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

      
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setCodigo("TICK-A1B2C3D4");
        ticket.setCompraId(5L);
        ticket.setUsuarioId(2L);
        ticket.setEscenarioId(1L);
        ticket.setFechaAsistencia(LocalDateTime.now().plusDays(10));

  
        requestDto = new TicketRequestDTO();
        requestDto.setCompraId(5L);
        requestDto.setUsuarioId(2L);
        requestDto.setEscenarioId(1L);
        requestDto.setFechaAsistencia(LocalDateTime.now().plusDays(10));

     
        auth = new UsernamePasswordAuthenticationToken("2", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        TicketController controller = new TicketController(ticketService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testCrearTicket() throws Exception {
        when(ticketService.guardar(any(Ticket.class))).thenReturn(ticket);

        mockMvc.perform(post("/tickets")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("TICK-A1B2C3D4"))
                .andExpect(jsonPath("$.usuarioId").value(2L));
    }

    @Test
    public void testListarMisTickets() throws Exception {
        when(ticketService.listarPorUsuario(2L)).thenReturn(List.of(ticket));

        mockMvc.perform(get("/tickets/me")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].codigo").value("TICK-A1B2C3D4"));
    }

    @Test
    public void testListarTodos() throws Exception {
        when(ticketService.listarTodos()).thenReturn(List.of(ticket));

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(ticketService.buscarPorId(1L)).thenReturn(ticket);

        mockMvc.perform(get("/tickets/1")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("TICK-A1B2C3D4"));
    }

    @Test
    public void testObtenerPorCodigo() throws Exception {
        when(ticketService.buscarPorCodigo("TICK-A1B2C3D4")).thenReturn(ticket);

        mockMvc.perform(get("/tickets/codigo/TICK-A1B2C3D4")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("TICK-A1B2C3D4"));
    }

    @Test
    public void testActualizar() throws Exception {
        when(ticketService.buscarPorId(1L)).thenReturn(ticket);
        when(ticketService.actualizar(eq(1L), any(Ticket.class))).thenReturn(ticket);

        mockMvc.perform(put("/tickets/1")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testEliminar() throws Exception {
        when(ticketService.buscarPorId(1L)).thenReturn(ticket);
        doNothing().when(ticketService).eliminar(1L);

        mockMvc.perform(delete("/tickets/1")
                        .principal(auth))
                .andExpect(status().isNoContent());

        verify(ticketService, times(1)).eliminar(1L);
    }
}