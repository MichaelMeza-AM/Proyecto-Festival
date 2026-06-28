package com.festival.presentacion_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.festival.presentacion_service.model.Presentacion;
import com.festival.presentacion_service.repository.PresentacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PresentacionServiceTest {

    private PresentacionService presentacionService;
    
    @Mock 
    private PresentacionRepository presentacionRepository;
    @Mock 
    private WebClient webClient;
    @Mock 
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock 
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock 
    private WebClient.ResponseSpec responseSpec;

    private Presentacion presentacion;
    private LocalDateTime fechaTest;

    @BeforeEach
    void setUp() throws Exception {
        presentacionService = new PresentacionService(presentacionRepository, webClient);
        
        // Usamos Reflection para inyectar los paths de las @Value (exactamente como el profesor)
        Field artistaPathField = PresentacionService.class.getDeclaredField("artistaPath");
        artistaPathField.setAccessible(true);
        artistaPathField.set(presentacionService, "http://api/artista/exists/%d");

        Field escenarioPathField = PresentacionService.class.getDeclaredField("escenarioPath");
        escenarioPathField.setAccessible(true);
        escenarioPathField.set(presentacionService, "http://api/escenario/exists/%d");

        fechaTest = LocalDateTime.of(2026, 11, 20, 17, 0);
        presentacion = new Presentacion(1L, 4L, 1L, fechaTest, 90);
    }

    // Método auxiliar para simular que WebClient devuelve TRUE al validar dependencias
    private void mockDependenciasExistentes() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        // Retorna TRUE tanto para el artista como para el escenario
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(Boolean.TRUE));
    }

    @Test
    void testGuardar() {
        mockDependenciasExistentes();
        when(presentacionRepository.save(any(Presentacion.class))).thenReturn(presentacion);
        
        Presentacion resultado = presentacionService.guardar(presentacion);
        
        assertNotNull(resultado);
        assertEquals(4L, resultado.getArtistaId());
        verify(presentacionRepository).save(any(Presentacion.class));
    }

    @Test
    void testListar() {
        when(presentacionRepository.findAll()).thenReturn(List.of(presentacion));
        List<Presentacion> lista = presentacionService.listar();
        
        assertNotNull(lista);
        assertEquals(1, lista.size());
        verify(presentacionRepository).findAll();
    }

    @Test
    void testBuscarPorId() {
        when(presentacionRepository.findById(1L)).thenReturn(Optional.of(presentacion));
        Optional<Presentacion> resultado = presentacionService.buscarPorId(1L);
        
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(presentacionRepository).findById(1L);
    }

    @Test
    void testActualizar() {
        mockDependenciasExistentes();
        when(presentacionRepository.findById(1L)).thenReturn(Optional.of(presentacion));
        when(presentacionRepository.save(any(Presentacion.class))).thenReturn(presentacion);

        Optional<Presentacion> resultado = presentacionService.actualizar(1L, presentacion);

        assertTrue(resultado.isPresent());
        verify(presentacionRepository).findById(1L);
        verify(presentacionRepository).save(any(Presentacion.class));
    }

    @Test
    void testEliminar() {
        when(presentacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(presentacionRepository).deleteById(1L);
        
        boolean eliminado = presentacionService.eliminar(1L);
        
        assertTrue(eliminado);
        verify(presentacionRepository).deleteById(1L);
    }

    @Test
    void testBuscarPorDia() {
        when(presentacionRepository.findByFechaHoraBetween(any(), any())).thenReturn(List.of(presentacion));
        
        List<Presentacion> resultado = presentacionService.buscarPorDia(LocalDate.of(2026, 11, 20));
        
        assertFalse(resultado.isEmpty());
        verify(presentacionRepository).findByFechaHoraBetween(any(), any());
    }
}