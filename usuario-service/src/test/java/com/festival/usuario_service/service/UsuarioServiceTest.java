package com.festival.usuario_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.festival.usuario_service.exception.BadRequestException;
import com.festival.usuario_service.model.Usuario;
import com.festival.usuario_service.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "Juan Perez", "juan@test.com", "12345678-9", LocalDate.of(1995, 5, 20));
    }

    @Test
    void testGuardarExito() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRut(anyString())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.guardar(usuario);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombre());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testGuardarFallaPorEmailDuplicado() {
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> usuarioService.guardar(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testGuardarFallaPorRutDuplicado() {
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(usuarioRepository.existsByRut("12345678-9")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> usuarioService.guardar(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        List<Usuario> lista = usuarioService.listarUsuarios();
        
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }

    @Test
    void testBuscarPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Usuario resultado = usuarioService.buscarPorId(1L);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testExistePorId() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        assertTrue(usuarioService.existePorId(1L));
    }

    @Test
    void testActualizarUsuarioExito() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Simulamos que actualiza con datos que no colisionan
         Usuario resultado = usuarioService.actualizarUsuario(
                1L, "Juan Actualizado", "juan@test.com", "12345678-9", LocalDate.of(1995, 5, 20)
        );

        assertNotNull(resultado);
        assertEquals("Juan Actualizado", resultado.getNombre());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminarUsuario(1L);
        
        verify(usuarioRepository).deleteById(1L);
    }
}