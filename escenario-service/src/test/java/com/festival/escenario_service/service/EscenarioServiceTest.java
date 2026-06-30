package com.festival.escenario_service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.festival.escenario_service.model.Escenario;
import com.festival.escenario_service.model.Zona;
import com.festival.escenario_service.repository.EscenarioRepository;
import com.festival.escenario_service.repository.ZonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EscenarioServiceTest {

    @Mock
    private EscenarioRepository escenarioRepository;

    @Mock
    private ZonaRepository zonaRepository;

    @InjectMocks
    private EscenarioService escenarioService;

    private Escenario escenario;
    private Zona zona;

    @BeforeEach
    void setUp() {
        zona = new Zona(1L, "Explanada Central", "Principal");
        escenario = new Escenario(1L, "Main Stage", "Puerta A", 40000, 65000, zona);
    }

    @Test
    void testGuardar() {
        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zona));
        when(escenarioRepository.save(any(Escenario.class))).thenReturn(escenario);

        Escenario resultado = escenarioService.guardar(escenario);

        assertNotNull(resultado);
        assertEquals("Main Stage", resultado.getNombre());
        verify(zonaRepository, times(1)).findById(1L);
        verify(escenarioRepository, times(1)).save(escenario);
    }

    @Test
    void testListar() {
        when(escenarioRepository.findAll()).thenReturn(List.of(escenario));
        List<Escenario> resultado = escenarioService.listar();
        assertFalse(resultado.isEmpty());
    }

    @Test
    void testBuscarPorId() {
        when(escenarioRepository.findById(1L)).thenReturn(Optional.of(escenario));
        Escenario resultado = escenarioService.buscarPorId(1L);
        assertNotNull(resultado);
    }

    @Test
    void testActualizar() {
        when(escenarioRepository.findById(1L)).thenReturn(Optional.of(escenario));
        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zona));
        when(escenarioRepository.save(any(Escenario.class))).thenAnswer(i -> i.getArgument(0));

        Escenario resultado = escenarioService.actualizar(1L, escenario);

        assertNotNull(resultado);
        verify(escenarioRepository, times(1)).save(any(Escenario.class));
    }

    @Test
    void testEliminar() {
        when(escenarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(escenarioRepository).deleteById(1L);

        escenarioService.eliminar(1L);

        // Verifica que realmente se haya llamado al método de borrar del repositorio
        verify(escenarioRepository, times(1)).deleteById(1L);
    }
}