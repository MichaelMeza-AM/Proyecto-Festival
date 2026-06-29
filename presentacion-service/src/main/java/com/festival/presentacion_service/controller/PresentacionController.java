package com.festival.presentacion_service.controller;

import com.festival.presentacion_service.dto.PresentacionRequestDTO;
import com.festival.presentacion_service.dto.PresentacionResponseDTO;
import com.festival.presentacion_service.exception.ResourceNotFoundException;
import com.festival.presentacion_service.model.Presentacion;
import com.festival.presentacion_service.service.PresentacionService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/presentaciones")
public class PresentacionController {

    private static final Logger logger = LoggerFactory.getLogger(PresentacionController.class);
    private final PresentacionService presentacionService;

    public PresentacionController(PresentacionService presentacionService) {
        this.presentacionService = presentacionService;
    }


    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existePresentacion(@PathVariable Long id) {
        logger.info("GET /presentaciones/exists/{} - Solicitud de validación de existencia", id);
        boolean existe = presentacionService.buscarPorId(id).isPresent();
        return ResponseEntity.ok(existe);
    }

    @GetMapping
    public ResponseEntity<List<PresentacionResponseDTO>> listar() {
        logger.info("GET /presentaciones - Solicitud para listar todas las presentaciones");
        List<PresentacionResponseDTO> dtos = presentacionService.listar().stream()
                .map(presentacionService::obtenerDetalle)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresentacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /presentaciones/{} - Solicitud para buscar por ID", id);
        Presentacion presentacion = presentacionService.buscarPorId(id)
                .orElseThrow(() -> {
                    logger.warn("Búsqueda fallida: La presentación ID {} no fue encontrada", id);
                    return new ResourceNotFoundException("No se encontró la presentación con ID " + id);
                });
        
        return ResponseEntity.ok(presentacionService.obtenerDetalle(presentacion));
    }

    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<PresentacionResponseDTO>> buscarPorGenero(@PathVariable String genero) {
        List<PresentacionResponseDTO> dtos = presentacionService.buscarPorGeneroMusical(genero).stream()
                .map(presentacionService::obtenerDetalle)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/dia")
    public ResponseEntity<List<PresentacionResponseDTO>> buscarPorDia(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {
        
        List<PresentacionResponseDTO> dtos = presentacionService.buscarPorDia(fecha).stream()
                .map(presentacionService::obtenerDetalle)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // Endpoints administrativos para el admi

    @PostMapping
    public ResponseEntity<PresentacionResponseDTO> crear(@Valid @RequestBody PresentacionRequestDTO dto) {
        logger.info("POST /presentaciones - Petición de creación: artistaId={}, escenarioId={}", 
                    dto.getArtistaId(), dto.getEscenarioId());

        Presentacion nueva = presentacionService.guardar(dto.toModel());
        
        logger.info("Petición POST procesada con éxito. Presentación ID {} creada.", nueva.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(presentacionService.obtenerDetalle(nueva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresentacionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PresentacionRequestDTO dto) {
        logger.info("PUT /presentaciones/{} - Petición de actualización: artistaId={}, escenarioId={}", 
                    id, dto.getArtistaId(), dto.getEscenarioId());

        Presentacion p = dto.toModel();
        
        Presentacion actualizada = presentacionService.actualizar(id, p)
                .orElseThrow(() -> {
                    logger.warn("Actualización fallida: La presentación ID {} no existe", id);
                    return new ResourceNotFoundException("No se puede actualizar. Presentación ID " + id + " no existe.");
                });

        logger.info("Petición PUT procesada con éxito para ID {}", id);
        return ResponseEntity.ok(presentacionService.obtenerDetalle(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /presentaciones/{} - Petición de eliminación", id);
        
        if (!presentacionService.eliminar(id)) {
            logger.warn("Eliminación fallida: La presentación ID {} no existe", id);
            throw new ResourceNotFoundException("No se puede eliminar. Presentación ID " + id + " no existe.");
        }
        
        logger.info("Petición DELETE procesada con éxito. Presentación ID {} eliminada.", id);
        return ResponseEntity.noContent().build();
    }
}