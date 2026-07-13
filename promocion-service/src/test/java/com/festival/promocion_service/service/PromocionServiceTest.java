package com.festival.promocion_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.festival.promocion_service.dto.PromocionResponseDTO;
import com.festival.promocion_service.exception.BadRequestException;
import com.festival.promocion_service.exception.ResourceNotFoundException;
import com.festival.promocion_service.model.Promocion;
import com.festival.promocion_service.repository.PromocionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private PromocionService promocionService;

    private Promocion promocionValida;

    @BeforeEach
    void setUp() {
        // Inicializamos un cupón que por defecto sea plenamente vigente hoy
        promocionValida = new Promocion(
            1L, 
            "PROMO2026", 
            20, 
            LocalDateTime.now().minusDays(1), // Empezó ayer
            LocalDateTime.now().plusDays(5),  // Vence en 5 días
            true
        );
    }

    @Test
    void testGuardar_Exito() {
        when(promocionRepository.findByCodigo("PROMO2026")).thenReturn(Optional.empty());
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocionValida);

        Promocion resultado = promocionService.guardar(promocionValida);

        assertNotNull(resultado);
        assertEquals("PROMO2026", resultado.getCodigo());
        verify(promocionRepository, times(1)).save(promocionValida);
    }

    @Test
    void testGuardar_CodigoDuplicado_LanzaBadRequestException() {
        when(promocionRepository.findByCodigo("PROMO2026")).thenReturn(Optional.of(promocionValida));

        assertThrows(BadRequestException.class, () -> promocionService.guardar(promocionValida));
        verify(promocionRepository, never()).save(any(Promocion.class));
    }

    @Test
    void testGuardar_FechasIncoherentes_LanzaBadRequestException() {
        // Forzamos que la fecha fin sea antes que la de inicio
        promocionValida.setFechaInicio(LocalDateTime.now().plusDays(5));
        promocionValida.setFechaFin(LocalDateTime.now().plusDays(1));

        assertThrows(BadRequestException.class, () -> promocionService.guardar(promocionValida));
        verify(promocionRepository, never()).save(any(Promocion.class));
    }

    @Test
    void testListarTodas() {
        when(promocionRepository.findAll()).thenReturn(List.of(promocionValida));

        List<Promocion> resultado = promocionService.listarTodas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(promocionRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_Exito() {
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocionValida));

        Promocion resultado = promocionService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testBuscarPorId_NoEncontrado_LanzaResourceNotFoundException() {
        when(promocionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> promocionService.buscarPorId(1L));
    }

    @Test
    void testEliminar_Exito() {
        when(promocionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(promocionRepository).deleteById(1L);

        promocionService.eliminar(1L);

        verify(promocionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testActualizar_Exito() {
        Promocion detallesNuevos = new Promocion(null, "PROMO_MODIFICADA", 25, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5), true);
        
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocionValida));
        when(promocionRepository.findByCodigo("PROMO_MODIFICADA")).thenReturn(Optional.empty());
        when(promocionRepository.save(any(Promocion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Promocion resultado = promocionService.actualizar(1L, detallesNuevos);

        assertNotNull(resultado);
        assertEquals("PROMO_MODIFICADA", resultado.getCodigo());
        assertEquals(25, resultado.getPorcentajeDescuento());
    }

    // --- PRUEBAS DE LA LÓGICA DE NEGOCIO: VALIDAR CUPONES ---

    @Test
    void testValidarPromocion_CupónPlenamenteVálido() {
        when(promocionRepository.findByCodigo("PROMO2026")).thenReturn(Optional.of(promocionValida));

        PromocionResponseDTO response = promocionService.validarPromocion("PROMO2026");

        assertTrue(response.getEsValido());
        assertEquals(20, response.getPorcentajeDescuento());
        assertTrue(response.getMensaje().contains("éxito"));
    }

    @Test
    void testValidarPromocion_CupónInactivo() {
        promocionValida.setActivo(false); // Pausado manualmente por el ADMIN
        when(promocionRepository.findByCodigo("PROMO2026")).thenReturn(Optional.of(promocionValida));

        PromocionResponseDTO response = promocionService.validarPromocion("PROMO2026");

        assertFalse(response.getEsValido());
        assertEquals(false, response.getActivo());
        assertTrue(response.getMensaje().contains("desactivada"));
    }

    @Test
    void testValidarPromocion_CupónNoVigenteAún() {
        promocionValida.setFechaInicio(LocalDateTime.now().plusDays(2)); // Inicia en 2 días
        promocionValida.setFechaFin(LocalDateTime.now().plusDays(10));
        when(promocionRepository.findByCodigo("PROMO2026")).thenReturn(Optional.of(promocionValida));

        PromocionResponseDTO response = promocionService.validarPromocion("PROMO2026");

        assertFalse(response.getEsValido());
        assertTrue(response.getMensaje().contains("no está vigente"));
    }

    @Test
    void testValidarPromocion_CupónExpirado() {
        promocionValida.setFechaInicio(LocalDateTime.now().minusDays(10));
        promocionValida.setFechaFin(LocalDateTime.now().minusDays(2)); // Venció hace 2 días
        when(promocionRepository.findByCodigo("PROMO2026")).thenReturn(Optional.of(promocionValida));

        PromocionResponseDTO response = promocionService.validarPromocion("PROMO2026");

        assertFalse(response.getEsValido());
        assertTrue(response.getMensaje().contains("expirado"));
    }
}