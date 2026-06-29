package com.festival.artista_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.festival.artista_service.model.Artista;
import com.festival.artista_service.repository.ArtistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ArtistaServiceTest {

    @Mock
    private ArtistaRepository artistaRepository;

    @InjectMocks
    private ArtistaService artistaService;

    private Artista artista;

    @BeforeEach
    void setUp() {
        // Objeto base para las pruebas
        artista = new Artista(1L, "Los Prisioneros", "Banda icónica de rock chileno.", "Rock");
    }

    @Test
    void testGuardar() {
        when(artistaRepository.save(any(Artista.class))).thenReturn(artista);

        Artista resultado = artistaService.guardar(artista);

        assertNotNull(resultado);
        assertEquals("Los Prisioneros", resultado.getNombre());
        verify(artistaRepository, times(1)).save(artista);
    }

    @Test
    void testListar() {
        when(artistaRepository.findAll()).thenReturn(List.of(artista));

        List<Artista> resultado = artistaService.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Rock", resultado.get(0).getGeneroMusical());
        verify(artistaRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId() {
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));

        Optional<Artista> resultado = artistaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Los Prisioneros", resultado.get().getNombre());
        verify(artistaRepository, times(1)).findById(1L);
    }

    @Test
    void testEliminar() {
        doNothing().when(artistaRepository).deleteById(1L);

        artistaService.eliminar(1L);

        verify(artistaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testActualizar() {
        Artista detallesNuevos = new Artista(null, "Los Prisioneros Modificado", "Nueva biografía", "Rock-Pop");
        
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));
        // Simulamos que al guardar retorna el objeto modificado
        when(artistaRepository.save(any(Artista.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Artista> resultado = artistaService.actualizar(1L, detallesNuevos);

        assertTrue(resultado.isPresent());
        assertEquals("Los Prisioneros Modificado", resultado.get().getNombre());
        assertEquals("Nueva biografía", resultado.get().getBiografia());
        assertEquals("Rock-Pop", resultado.get().getGeneroMusical());
        verify(artistaRepository, times(1)).findById(1L);
        verify(artistaRepository, times(1)).save(any(Artista.class));
    }

    @Test
    void testObtenerIdsPorGenero() {
        when(artistaRepository.findIdsByGeneroMusical("Rock")).thenReturn(List.of(1L));

        List<Long> ids = artistaService.obtenerIdsPorGenero("Rock");

        assertNotNull(ids);
        assertEquals(1, ids.size());
        assertEquals(1L, ids.get(0));
        verify(artistaRepository, times(1)).findIdsByGeneroMusical("Rock");
    }

    @Test
    void eliminarArtista() {
        // Simula que el repositorio no hace nada (void) al borrar
        doNothing().when(artistaRepository).deleteById(1L);

        artistaService.eliminar(1L);

        verify(artistaRepository, times(1)).deleteById(1L);
    }
}