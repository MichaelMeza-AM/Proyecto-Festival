package com.festival.itinerario_service.controller;

import com.festival.itinerario_service.dto.ItinerarioDTO;
import com.festival.itinerario_service.dto.ItinerarioResponseDTO;
import com.festival.itinerario_service.exception.ForbiddenException;
import com.festival.itinerario_service.model.Itinerario;
import com.festival.itinerario_service.service.ItinerarioService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itinerarios")
public class ItinerarioController {

    private static final Logger logger = LoggerFactory.getLogger(ItinerarioController.class);
    private final ItinerarioService itinerarioService;

    public ItinerarioController(ItinerarioService itinerarioService) {
        this.itinerarioService = itinerarioService;
    }

    
    @PostMapping
    public ResponseEntity<ItinerarioResponseDTO> crearItinerario(@Valid @RequestBody ItinerarioDTO dto, Authentication auth) {
   
        Long userId = Long.parseLong(auth.getName());
        dto.setUsuarioId(userId);
        
        logger.info("POST /itinerarios - usuarioId={} (desde token), presentacionId={}", userId, dto.getPresentacionId());

        Itinerario nuevo = itinerarioService.guardar(dto.toModel());
        logger.info("Itinerario creado exitosamente id={}", nuevo.getId());

        ItinerarioResponseDTO respuestaEnriquecida = itinerarioService.obtenerDetalleEnriquecido(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaEnriquecida);
    }


    @GetMapping
    public ResponseEntity<List<ItinerarioResponseDTO>> listarTodos() {
        logger.info("GET /itinerarios - Listando tabla completa");
        
        List<ItinerarioResponseDTO> dtos = itinerarioService.listarTodos().stream()
                .map(itinerarioService::obtenerDetalleEnriquecido)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItinerarioResponseDTO> obtenerItinerarioPorId(@PathVariable Long id) {
        logger.info("GET /itinerarios/{} - Obteniendo itinerario individual", id);
        Itinerario itinerario = itinerarioService.buscarPorId(id);
        return ResponseEntity.ok(itinerarioService.obtenerDetalleEnriquecido(itinerario));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existeItinerario(@PathVariable Long id) {
        logger.info("GET /itinerarios/exists/{} - Validando existencia", id);
        return ResponseEntity.ok(itinerarioService.existePorId(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ItinerarioResponseDTO> actualizarItinerario(@PathVariable Long id, @Valid @RequestBody ItinerarioDTO dto, Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        logger.info("PUT /itinerarios/{} - Intento de actualización por usuarioId={}", id, userId);

        // Buscar primero en la base de datos plana para validar a quién le pertenece
        Itinerario existente = itinerarioService.buscarPorId(id);
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        // Validar propiedad: si no es ADMIN ni el dueño, se bloquea
        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            logger.warn("Acceso denegado: usuarioId={} intentó modificar itinerarioId={} de otro usuario", userId, id);
            throw new ForbiddenException("Acceso denegado: No tienes permiso sobre este itinerario");
        }

        // Mantenemos el dueño original intacto, sin importar qué mande en el JSON
        dto.setUsuarioId(existente.getUsuarioId());

        Itinerario actualizado = itinerarioService.actualizar(id, dto.toModel());
        logger.info("Itinerario ID: {} actualizado correctamente", id);
        
        return ResponseEntity.ok(itinerarioService.obtenerDetalleEnriquecido(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItinerario(@PathVariable Long id, Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        logger.info("DELETE /itinerarios/{} - Intento de eliminación por usuarioId={}", id, userId);

        Itinerario existente = itinerarioService.buscarPorId(id);

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            logger.warn("Acceso denegado: usuarioId={} intentó eliminar itinerarioId={} de otro usuario", userId, id);
            throw new ForbiddenException("Acceso denegado: No tienes permiso sobre este itinerario");
        }

        itinerarioService.eliminar(id);
        logger.info("Itinerario ID: {} eliminado con éxito", id);

        return ResponseEntity.noContent().build();
    }

 @GetMapping("/me")
    public ResponseEntity<List<ItinerarioResponseDTO>> listarMisItinerarios(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        logger.info("GET /itinerarios/me - Listando itinerarios para usuarioId={}", userId);

        List<Itinerario> itinerarios = itinerarioService.listarPorUsuario(userId);
        List<ItinerarioResponseDTO> dtos = itinerarios.stream()
                .map(itinerarioService::obtenerDetalleEnriquecido)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

   @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ItinerarioResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId, Authentication auth) {
        
        Long userIdToken = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !usuarioId.equals(userIdToken)) {
            throw new ForbiddenException("Acceso denegado: No puedes ver el itinerario de otra persona");
        }

        logger.info("GET /itinerarios/usuario/{}", usuarioId);
        List<Itinerario> itinerarios = itinerarioService.listarPorUsuario(usuarioId);
        List<ItinerarioResponseDTO> dtos = itinerarios.stream()
                .map(itinerarioService::obtenerDetalleEnriquecido)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }



}