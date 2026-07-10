package com.festival.promocion_service.controller;

import com.festival.promocion_service.dto.PromocionRequestDTO;
import com.festival.promocion_service.dto.PromocionResponseDTO;
import com.festival.promocion_service.model.Promocion;
import com.festival.promocion_service.service.PromocionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @PostMapping
    public ResponseEntity<PromocionResponseDTO> crearPromocion(@Valid @RequestBody PromocionRequestDTO requestDto) {
        Promocion nueva = promocionService.guardar(requestDto.toModel());
        return ResponseEntity.status(HttpStatus.CREATED).body(PromocionResponseDTO.fromModel(nueva));
    }

    @GetMapping
    public ResponseEntity<List<PromocionResponseDTO>> listarTodas() {
        List<Promocion> promociones = promocionService.listarTodas();
        List<PromocionResponseDTO> dtos = promociones.stream()
                .map(PromocionResponseDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromocionResponseDTO> obtenerPorId(@PathVariable Long id) {
        Promocion promocion = promocionService.buscarPorId(id);
        return ResponseEntity.ok(PromocionResponseDTO.fromModel(promocion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocionResponseDTO> actualizarPromocion(@PathVariable Long id, @Valid @RequestBody PromocionRequestDTO requestDto) {
        Promocion actualizada = promocionService.actualizar(id, requestDto.toModel());
        return ResponseEntity.ok(PromocionResponseDTO.fromModel(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPromocion(@PathVariable Long id) {   
        promocionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<PromocionResponseDTO> validarPromocion(@PathVariable String codigo) {
        PromocionResponseDTO resultado = promocionService.validarPromocion(codigo);
        return ResponseEntity.ok(resultado);
    }
}