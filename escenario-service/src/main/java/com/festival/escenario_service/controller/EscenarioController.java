package com.festival.escenario_service.controller;

import com.festival.escenario_service.dto.EscenarioDTO;
import com.festival.escenario_service.exception.ResourceNotFoundException;
import com.festival.escenario_service.model.Escenario;
import com.festival.escenario_service.service.EscenarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/escenarios")
public class EscenarioController {

    private final EscenarioService escenarioService;

    public EscenarioController(EscenarioService escenarioService) {
        this.escenarioService = escenarioService;
    }

    @GetMapping
    public ResponseEntity<List<EscenarioDTO>> listar() {
        List<Escenario> escenarios = escenarioService.listar();
        List<EscenarioDTO> dtos = escenarios.stream()
                .map(EscenarioDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscenarioDTO> obtenerPorId(@PathVariable Long id) {
        Escenario escenario = escenarioService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el escenario con ID " + id));
        
        return ResponseEntity.ok(EscenarioDTO.fromModel(escenario));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existeEscenario(@PathVariable Long id) {
        return ResponseEntity.ok(escenarioService.existePorId(id));
    }

    @PostMapping
    public ResponseEntity<EscenarioDTO> crearEscenario(@Valid @RequestBody EscenarioDTO escenarioDto) {
        Escenario nuevo = escenarioService.guardar(escenarioDto.toModel());
        // Código HTTP 201 Created y retorno directo del DTO limpio
        return ResponseEntity.status(HttpStatus.CREATED).body(EscenarioDTO.fromModel(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscenarioDTO> actualizarEscenario(@PathVariable Long id, @Valid @RequestBody EscenarioDTO escenarioDto) {
        Escenario actualizado = escenarioService.actualizar(id, escenarioDto.toModel())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar. El escenario con ID " + id + " no existe."));
        
        // Código HTTP 200 OK y retorno directo del DTO limpio
        return ResponseEntity.ok(EscenarioDTO.fromModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEscenario(@PathVariable Long id) {
        if (!escenarioService.existePorId(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. El escenario con ID " + id + " no existe.");
        }
        
        escenarioService.eliminar(id);
        // Respuesta estándar 204 No Content para eliminaciones exitosas
        return ResponseEntity.noContent().build();
    }
}