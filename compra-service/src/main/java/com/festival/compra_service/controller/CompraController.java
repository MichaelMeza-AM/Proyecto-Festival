package com.festival.compra_service.controller;

import com.festival.compra_service.dto.CompraRequestDTO;
import com.festival.compra_service.dto.CompraResponseDTO;
import com.festival.compra_service.exception.ForbiddenException;
import com.festival.compra_service.exception.ResourceNotFoundException;
import com.festival.compra_service.model.Compra;
import com.festival.compra_service.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<CompraResponseDTO> procesarCompra(@Valid @RequestBody CompraRequestDTO request, Authentication auth) {
        Long usuarioId = Long.parseLong(auth.getName());
        Compra nuevaCompra = compraService.guardarCompra(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CompraResponseDTO.fromModel(nuevaCompra));
    }

    @GetMapping("/me")
    public ResponseEntity<List<CompraResponseDTO>> listarMisCompras(Authentication auth) {
        Long usuarioId = Long.parseLong(auth.getName());
        List<CompraResponseDTO> misCompras = compraService.listarComprasPorUsuario(usuarioId).stream()
                .map(CompraResponseDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(misCompras);
    }

    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listarTodos() {
        List<CompraResponseDTO> dtos = compraService.listarTodos().stream()
                .map(CompraResponseDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerPorId(@PathVariable Long id, Authentication auth) {
        Compra existente = compraService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la compra con ID " + id));

        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para ver esta compra");
        }

        return ResponseEntity.ok(CompraResponseDTO.fromModel(existente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CompraRequestDTO request, Authentication auth) {
        Compra existente = compraService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la compra con ID " + id));

        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para modificar esta compra");
        }

        Compra actualizada = compraService.actualizar(id, request)
                .orElseThrow(() -> new ResourceNotFoundException("Error al actualizar la compra con ID " + id));

        return ResponseEntity.ok(CompraResponseDTO.fromModel(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        Compra existente = compraService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la compra con ID " + id));

        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        // Seguridad: Solo el dueño o el admin pueden anular/borrar la compra
        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para eliminar esta compra");
        }

        compraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}