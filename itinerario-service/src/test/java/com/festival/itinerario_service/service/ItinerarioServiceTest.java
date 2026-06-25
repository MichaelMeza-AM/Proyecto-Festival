package com.festival.itinerario_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.festival.itinerario_service.model.Itinerario;
import com.festival.itinerario_service.repository.ItinerarioRepository;
import com.festival.itinerario_service.dto.PresentacionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.festival.itinerario_service.exception.BadRequestException;
import com.festival.itinerario_service.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ItinerarioServiceTest {
    private ItinerarioService itinerarioService;
    
    @Mock
    private ItinerarioRepository itinerarioRepository;
    
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() throws Exception {
        itinerarioService = new ItinerarioService(itinerarioRepository, webClient);
        
        Field fUsuario = ItinerarioService.class.getDeclaredField("usuarioPath");
        fUsuario.setAccessible(true);
        fUsuario.set(itinerarioService, "http://api/usuarios/%d");

        Field fPresentacion = ItinerarioService.class.getDeclaredField("presentacionPath");
        fPresentacion.setAccessible(true);
        fPresentacion.set(itinerarioService, "http://api/presentaciones/%d");

        Field fDetalle = ItinerarioService.class.getDeclaredField("presentacionDetallePath");
        fDetalle.setAccessible(true);
        fDetalle.set(itinerarioService, "http://api/presentaciones/detalle/%d");
    }

    private void mockDependenciasExistentes() {
        // Le decimos al mock que responda a TODAS las llamadas consecutivas de WebClient
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        // 1. Cuando pida un Boolean (existeUsuario o existePresentacion)
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(Boolean.TRUE));
        
        // 2. Cuando pida el DTO para validar el cruce de horarios en el Bucle
        PresentacionDTO dto = new PresentacionDTO();
        dto.setId(5L);
        dto.setFechaHora(LocalDateTime.now().plusDays(1)); // Mañana
        dto.setDuracionMinutos(120);
        when(responseSpec.bodyToMono(PresentacionDTO.class)).thenReturn(Mono.just(dto));
    }

    @Test
    void testGuardar() {
        mockDependenciasExistentes();
        
        Itinerario itinerario = new Itinerario();
        itinerario.setUsuarioId(1L);
        itinerario.setPresentacionId(5L);
        
        // Fingimos que no tiene itinerarios previos para evitar el bucle for
        when(itinerarioRepository.existsByUsuarioIdAndPresentacionId(1L, 5L)).thenReturn(false);
        when(itinerarioRepository.findByUsuarioId(1L)).thenReturn(List.of());
        when(itinerarioRepository.save(any(Itinerario.class))).thenReturn(itinerario);
        
        Itinerario resultado = itinerarioService.guardar(itinerario);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getUsuarioId());
        verify(itinerarioRepository).save(any(Itinerario.class));
    }

    @Test
    void testListar() {
        Itinerario it = new Itinerario();
        it.setId(1L);
        
        when(itinerarioRepository.findAll()).thenReturn(List.of(it));
        List<Itinerario> itinerarios = itinerarioService.listarTodos();
        
        assertNotNull(itinerarios);
        assertEquals(1, itinerarios.size());
        verify(itinerarioRepository).findAll();
    }

    @Test
    void testObtenerPorId() {
        Long id = 1L;
        Itinerario itinerario = new Itinerario();
        itinerario.setId(id);
        
        when(itinerarioRepository.findById(id)).thenReturn(Optional.of(itinerario));
        Optional<Itinerario> resultado = itinerarioService.buscarPorId(id);
        
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        verify(itinerarioRepository).findById(id);
    }

    @Test
    void testActualizar() {
        mockDependenciasExistentes();
        Long id = 1L;
        
        Itinerario existente = new Itinerario();
        existente.setId(id);
        existente.setUsuarioId(1L);
        existente.setPresentacionId(5L);
        
        Itinerario cambios = new Itinerario();
        cambios.setUsuarioId(1L);
        cambios.setPresentacionId(10L); 
        
        when(itinerarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(itinerarioRepository.existsByUsuarioIdAndPresentacionId(1L, 10L)).thenReturn(false);
        
        // Tu código real llama a findByUsuarioId dentro de validarCruceHorarios()
        when(itinerarioRepository.findByUsuarioId(1L)).thenReturn(List.of(existente));
        
        when(itinerarioRepository.save(any(Itinerario.class))).thenReturn(cambios);
        Optional<Itinerario> resultado = itinerarioService.actualizar(id, cambios);
        
        assertTrue(resultado.isPresent());
        assertEquals(10L, resultado.get().getPresentacionId());
        
        verify(itinerarioRepository).findById(id);
        verify(itinerarioRepository).save(any(Itinerario.class));
    }

    @Test
    void testEliminar() {
        Long id = 1L;
        when(itinerarioRepository.existsById(id)).thenReturn(true);
        boolean eliminado = itinerarioService.eliminar(id);
        assertTrue(eliminado);
        verify(itinerarioRepository).deleteById(id);
    }


    // ====================================================================
    // PRUEBAS DE REGLAS DE NEGOCIO (Equivalentes a cálculo de IVA/Descuento)
    // ====================================================================

    @Test
    void testReglaNegocio_UsuarioInexistente() {
        // Simulamos que el WebClient va al otro microservicio y no encuentra al usuario
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(Boolean.FALSE));

        Itinerario itinerario = new Itinerario();
        itinerario.setUsuarioId(99L); // ID inventado
        itinerario.setPresentacionId(5L);

        // Verificamos que el sistema detecte la trampa y lance la excepción correcta
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            itinerarioService.guardar(itinerario);
        });
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

@Test
    void testReglaNegocio_EvitarDuplicados() {
        // En lugar de cargar todas las dependencias, solo fingimos la validación inicial (Boolean)
        // para que Mockito no se enoje por preparar datos de horarios que no se van a usar.
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(Boolean.TRUE));

        Itinerario itinerario = new Itinerario();
        itinerario.setUsuarioId(1L);
        itinerario.setPresentacionId(5L);

        // Le decimos a la base de datos que este usuario YA TIENE esta presentación
        when(itinerarioRepository.existsByUsuarioIdAndPresentacionId(1L, 5L)).thenReturn(true);

        // Verificamos que el sistema bloquee el guardado por duplicidad
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            itinerarioService.guardar(itinerario);
        });
        assertEquals("Esta presentación ya está en tu itinerario.", exception.getMessage());
    }

    @Test
    void testReglaNegocio_CruceDeHorarios() {
        // Configuramos el WebClient para que diga que el usuario y la presentación existen
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(Boolean.TRUE));

        // Creamos una presentación que será la que causa el problema (Mismo horario para todo)
        PresentacionDTO presentacionCruce = new PresentacionDTO();
        presentacionCruce.setId(5L);
        presentacionCruce.setNombreArtista("Artista Conflicto");
        presentacionCruce.setFechaHora(LocalDateTime.now()); // Hora actual
        presentacionCruce.setDuracionMinutos(120);

        // El WebClient devolverá esta presentación para las validaciones de horario
        when(responseSpec.bodyToMono(PresentacionDTO.class)).thenReturn(Mono.just(presentacionCruce));

        // Simulamos que el usuario ya tiene un itinerario guardado a esta misma hora
        Itinerario itinerarioExistente = new Itinerario();
        itinerarioExistente.setId(10L);
        itinerarioExistente.setUsuarioId(1L);
        itinerarioExistente.setPresentacionId(2L);

        when(itinerarioRepository.existsByUsuarioIdAndPresentacionId(1L, 5L)).thenReturn(false);
        when(itinerarioRepository.findByUsuarioId(1L)).thenReturn(List.of(itinerarioExistente));

        // Intentamos guardar un itinerario NUEVO a la misma hora
        Itinerario nuevoItinerario = new Itinerario();
        nuevoItinerario.setUsuarioId(1L);
        nuevoItinerario.setPresentacionId(5L);

        // Verificamos que el código calcule el choque de minutos y lance el error
        assertThrows(BadRequestException.class, () -> {
            itinerarioService.guardar(nuevoItinerario);
        });
    }
}