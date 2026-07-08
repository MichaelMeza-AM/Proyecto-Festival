package com.festival.pago_service.controller;

import com.festival.pago_service.dto.PagoRequestDTO;
import com.festival.pago_service.dto.PagoResponseDTO;
import com.festival.pago_service.exception.ForbiddenException;
import com.festival.pago_service.model.Pago;
import com.festival.pago_service.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoResponseDTO> procesarPago(
            @RequestHeader("Authorization") String tokenAuth,
            Authentication auth,
            @Valid @RequestBody PagoRequestDTO request) {
        
      
        Long usuarioId = Long.parseLong(auth.getName());
        
        Pago pagoGuardado = pagoService.procesarPago(request, tokenAuth, usuarioId);
        PagoResponseDTO response = PagoResponseDTO.fromModel(pagoGuardado);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        List<PagoResponseDTO> pagos = pagoService.listarTodos().stream()
                .map(PagoResponseDTO::fromModel)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> buscarPorId(@PathVariable Long id, Authentication auth) {
        Pago existente = pagoService.buscarPorId(id);
        
        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para ver este recibo de pago.");
        }

        return ResponseEntity.ok(PagoResponseDTO.fromModel(existente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody PagoRequestDTO request, 
            Authentication auth) {
        
        Pago existente = pagoService.buscarPorId(id);
        
        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para modificar este pago.");
        }
        
        Pago pagoActualizado = pagoService.actualizar(id, request);
        return ResponseEntity.ok(PagoResponseDTO.fromModel(pagoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        Pago existente = pagoService.buscarPorId(id);
        
        Long userId = Long.parseLong(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !existente.getUsuarioId().equals(userId)) {
            throw new ForbiddenException("Acceso denegado: No tienes permiso para eliminar este pago.");
        }

        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}