package com.festival.compra_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.festival.compra_service.dto.CompraRequestDTO;
import com.festival.compra_service.model.Compra;
import com.festival.compra_service.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {
    
    private CompraService compraService;
    
    @Mock
    private CompraRepository compraRepository;

    @BeforeEach
    void setUp() {
        compraService = new CompraService(compraRepository);
    }

    @Test
    void testGuardarCompra() {
        CompraRequestDTO request = new CompraRequestDTO();
        request.setEscenarioId(1L);
        request.setCantidad(2);
        request.setFechaAsistencia(LocalDateTime.now().plusDays(5)); 

        Compra compraGuardada = new Compra();
        compraGuardada.setId(1L);
        compraGuardada.setUsuarioId(2L);
        compraGuardada.setEscenarioId(1L);
        compraGuardada.setCantidad(2);
        compraGuardada.setFechaAsistencia(LocalDateTime.now().plusDays(5));
        
        when(compraRepository.save(any(Compra.class))).thenReturn(compraGuardada);
        
        Compra resultado = compraService.guardarCompra(2L, request);
        
        assertNotNull(resultado);
        assertEquals(2L, resultado.getUsuarioId());
        assertEquals(1L, resultado.getEscenarioId());
        verify(compraRepository).save(any(Compra.class));
    }

    @Test
    void testListarComprasPorUsuario() {
        Compra compra = new Compra();
        compra.setId(1L);
        compra.setUsuarioId(2L);
        
        when(compraRepository.findByUsuarioId(2L)).thenReturn(List.of(compra));
        
        List<Compra> resultado = compraService.listarComprasPorUsuario(2L);
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(compraRepository).findByUsuarioId(2L);
    }

    @Test
    void testListarTodos() {
        Compra compra = new Compra();
        compra.setId(1L);
        
        when(compraRepository.findAll()).thenReturn(List.of(compra));
        
        List<Compra> resultado = compraService.listarTodos();
        
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(compraRepository).findAll();
    }

    @Test
    void testBuscarPorId() {
        Long id = 1L;
        Compra compra = new Compra();
        compra.setId(id);
        
        when(compraRepository.findById(id)).thenReturn(Optional.of(compra));
        
        Compra resultado = compraService.buscarPorId(id);
        
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        verify(compraRepository).findById(id);
    }

    @Test
    void testActualizar() {
        Long id = 1L;
        
        Compra existente = new Compra();
        existente.setId(id);
        existente.setEscenarioId(1L);
        existente.setCantidad(2);
        
        CompraRequestDTO cambios = new CompraRequestDTO();
        cambios.setEscenarioId(3L);
        cambios.setCantidad(5);
        cambios.setFechaAsistencia(LocalDateTime.now().plusDays(10)); // Corrección 
        
        Compra actualizada = new Compra();
        actualizada.setId(id);
        actualizada.setEscenarioId(3L);
        actualizada.setCantidad(5);
        
        when(compraRepository.findById(id)).thenReturn(Optional.of(existente));
        when(compraRepository.save(any(Compra.class))).thenReturn(actualizada);
        
        Compra resultado = compraService.actualizar(id, cambios);
        
        assertNotNull(resultado);
        assertEquals(3L, resultado.getEscenarioId());
        assertEquals(5, resultado.getCantidad());
        
        verify(compraRepository).findById(id);
        verify(compraRepository).save(any(Compra.class));
    }

    @Test
    void testEliminar() {
        Long id = 1L;
        when(compraRepository.existsById(id)).thenReturn(true);
        
        compraService.eliminar(id);
        
        verify(compraRepository).deleteById(id);
    }
}