package com.festival.escenario_service.controller;

import com.festival.escenario_service.dto.EscenarioDTO;
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
        Escenario escenario = escenarioService.buscarPorId(id);
        return ResponseEntity.ok(EscenarioDTO.fromModel(escenario));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existeEscenario(@PathVariable Long id) {
        return ResponseEntity.ok(escenarioService.existePorId(id));
    }

    @PostMapping
    public ResponseEntity<EscenarioDTO> crearEscenario(@Valid @RequestBody EscenarioDTO escenarioDto) {
        Escenario nuevo = escenarioService.guardar(escenarioDto.toModel());
        return ResponseEntity.status(HttpStatus.CREATED).body(EscenarioDTO.fromModel(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscenarioDTO> actualizarEscenario(@PathVariable Long id, @Valid @RequestBody EscenarioDTO escenarioDto) {
        Escenario actualizado = escenarioService.actualizar(id, escenarioDto.toModel());
        return ResponseEntity.ok(EscenarioDTO.fromModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEscenario(@PathVariable Long id) {
        escenarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}