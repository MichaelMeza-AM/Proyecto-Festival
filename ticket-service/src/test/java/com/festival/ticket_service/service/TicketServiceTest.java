package com.festival.ticket_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.festival.ticket_service.exception.ResourceNotFoundException;
import com.festival.ticket_service.model.Ticket;
import com.festival.ticket_service.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(ticketRepository);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setCodigo("TICK-A1B2C3D4");
        ticket.setCompraId(5L);
        ticket.setUsuarioId(2L);
        ticket.setEscenarioId(1L);
        ticket.setFechaAsistencia(LocalDateTime.now().plusDays(10));
    }

    @Test
    void testGuardar() {
        Ticket ticketNuevo = new Ticket();
        ticketNuevo.setCompraId(5L);
        ticketNuevo.setUsuarioId(2L);
        ticketNuevo.setEscenarioId(1L);

        when(ticketRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketService.guardar(ticketNuevo);

        assertNotNull(resultado);
        assertNotNull(resultado.getCodigo()); 
        assertEquals("TICK-A1B2C3D4", resultado.getCodigo());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void testListarTodos() {
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        List<Ticket> resultados = ticketService.listarTodos();

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        verify(ticketRepository).findAll();
    }

    @Test
    void testBuscarPorId_Exitoso() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Ticket resultado = ticketService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(ticketRepository).findById(1L);
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.buscarPorId(99L));
    }

    @Test
    void testBuscarPorCodigo() {
        when(ticketRepository.findByCodigo("TICK-A1B2C3D4")).thenReturn(Optional.of(ticket));

        Ticket resultado = ticketService.buscarPorCodigo("TICK-A1B2C3D4");

        assertNotNull(resultado);
        assertEquals("TICK-A1B2C3D4", resultado.getCodigo());
        verify(ticketRepository).findByCodigo("TICK-A1B2C3D4");
    }

    @Test
    void testListarPorUsuario() {
        when(ticketRepository.findByUsuarioId(2L)).thenReturn(List.of(ticket));

        List<Ticket> resultados = ticketService.listarPorUsuario(2L);

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals(2L, resultados.get(0).getUsuarioId());
        verify(ticketRepository).findByUsuarioId(2L);
    }

    @Test
    void testActualizar() {
        Ticket detallesNuevos = new Ticket();
        detallesNuevos.setFechaAsistencia(LocalDateTime.now().plusDays(20));

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketService.actualizar(1L, detallesNuevos);

        assertNotNull(resultado); 
        verify(ticketRepository).findById(1L);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void testEliminar_Exitoso() {
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(1L);

        ticketService.eliminar(1L);

        verify(ticketRepository).deleteById(1L);
    }

    @Test
    void testEliminar_NoEncontrado() {
        when(ticketRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> ticketService.eliminar(99L));
        verify(ticketRepository, never()).deleteById(anyLong()); 
    }
}